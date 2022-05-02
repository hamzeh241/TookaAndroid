package ir.tdaapp.tooka.views.fragments.pricealert

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.PriceAlertAdapter
import ir.tdaapp.tooka.databinding.FragmentAlertListBinding
import ir.tdaapp.tooka.viewmodels.PriceAlertListViewModel
import ir.tdaapp.tooka.viewmodels.PriceAlertListViewModel.AlertDisableState.*
import ir.tdaapp.tooka.views.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class AlertListFragment: BaseFragmentSecond(), CoroutineScope {

  private lateinit var binding: FragmentAlertListBinding

  private val viewModel by inject<PriceAlertListViewModel>()
  private lateinit var adapter: PriceAlertAdapter

  var userId: Int = 0

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    lifecycleScope.launchWhenResumed {
      userId = (requireActivity() as MainActivity).userPrefs.getUserId(requireContext())
      viewModel.getData(userId)
    }

    initList()
  }

  private fun initList() {
    adapter = PriceAlertAdapter({ model, pos ->
      launch {
        viewModel.deleteAlert(userId, model.id)
      }
      binding.alertList.isEnabled = false
    }, { model, pos ->
      launch {
        binding.alertList.isEnabled = false
        viewModel.toggleAlert(userId, model.id)
      }
    })
    binding.alertList.layoutManager = LinearLayoutManager(requireContext())
    binding.alertList.adapter = adapter
  }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

    binding.toolbar.title = resources.getString(R.string.alerts)
    binding.toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }
  }

  override fun initListeners(view: View) {
    binding.cardView.setOnClickListener {
      findNavController().navigate(AlertListFragmentDirections.actionAlertListFragmentToCoinsListFragment())
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  override fun initObservables() {
    viewModel.alertListState.observe(viewLifecycleOwner) {
      when (it) {
        PriceAlertListViewModel.AlertListState.OK -> return@observe
        PriceAlertListViewModel.AlertListState.LIST_EMPTY -> toast("list empty")
        PriceAlertListViewModel.AlertListState.INVALID_ARGS -> toast("invalid args")
        PriceAlertListViewModel.AlertListState.SERVER_ERROR -> toast("server error")
        PriceAlertListViewModel.AlertListState.NETWORK_ERROR -> toast("network error")
        PriceAlertListViewModel.AlertListState.UNKNOWN_ERROR -> toast("unknown error")
      }
    }
    viewModel.alertDeleteState.observe(viewLifecycleOwner) {
      handleErrors(it)
      binding.alertList.isEnabled = true
    }
    viewModel.alertDisableState.observe(viewLifecycleOwner) {
      handleErrors(it)
      binding.alertList.isEnabled = true
    }
    viewModel.alerts.observe(viewLifecycleOwner) {
      binding.alertList.isEnabled = true
      adapter.differ.submitList(it)
      binding.alertList.adapter = adapter
    }
  }

  private fun handleErrors(it: PriceAlertListViewModel.AlertDisableState) {
    @DrawableRes val icon: Int
    @ColorRes val color: Int
    @StringRes val text: Int

    when (it) {
      CHANGE_SUCCESS -> {
        icon = 0
        color = 0
        text = 0
      }
      NOT_FOUND -> {
        text = R.string.alert_not_found
        icon = R.drawable.ic_no_data
        color = R.color.red_200
      }
      DATABASE_EXCEPTION -> {
        text = R.string.user_database_error
        icon = R.drawable.ic_analytics_white_24dp
        color = R.color.red_200
      }
      UNPROCESSABLE_ENTITY -> {
        text = R.string.user_database_error
        icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        color = R.color.red_200
      }
      INVALID_ARGS -> {
        text = R.string.user_database_error
        icon = R.drawable.ic_no_data
        color = R.color.red_200
      }
      SERVER_ERROR -> {
        text = R.string.user_database_error
        icon = R.drawable.ic_dns_white_24dp
        color = R.color.red_200
      }
      NETWORK_ERROR -> {
        text = R.string.network_error_desc
        icon = R.drawable.ic_cloud_off_white_24dp
        color = R.color.red_200
      }
      UNKNOWN_ERROR -> {
        text = R.string.unknown_error_desc
        icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        color = R.color.red_200
      }
    }

    if (it != CHANGE_SUCCESS)
      customToast(
        resources.getString(text),
        color,
        icon
      )

    launch {
      viewModel.getData(
        userId
      )
    }
  }

  override fun onContextItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        requireActivity().onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun initErrors() = Unit

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentAlertListBinding.inflate(inflater, container, false)
    return binding
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("PriceAlertListJob")

  override fun onDestroy() {
    super.onDestroy()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }
}