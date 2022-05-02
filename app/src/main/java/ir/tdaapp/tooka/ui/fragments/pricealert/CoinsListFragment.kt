package ir.tdaapp.tooka.ui.fragments.pricealert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.CoinsListAdapter
import ir.tdaapp.tooka.databinding.FragmentCoinsListBinding
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.viewmodels.CoinsListViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond
import org.koin.android.ext.android.inject

class CoinsListFragment: BaseFragmentSecond() {

  private lateinit var binding: FragmentCoinsListBinding
  private lateinit var adapter: CoinsListAdapter

  private val viewModel by inject<CoinsListViewModel>()

  override fun init() {
    lifecycleScope.launchWhenCreated {
      viewModel.getData()
    }

    initList()
  }

  private fun initList() {
    adapter = CoinsListAdapter { clicked, pos ->
      findNavController().navigate(
        CoinsListFragmentDirections.actionCoinsListFragmentToInsertPriceAlertFragment(
          clicked.id
        )
      )
    }
    binding.list.layoutManager = LinearLayoutManager(requireContext())
    binding.list.adapter = adapter
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
  }

  override fun initListeners(view: View) {
  }

  override fun initObservables() {
    viewModel.list.observe(viewLifecycleOwner) {
      adapter.differ.submitList(it)
    }
  }

  override fun initErrors() {
    viewModel.error.observe(viewLifecycleOwner) {
      handleErrors(it)
    }
  }

  private fun handleErrors(it: NetworkErrors) {
    @DrawableRes val icon: Int
    @ColorRes val color: Int
    @StringRes val text: Int

    when (it) {
      NetworkErrors.SERVER_ERROR -> {
        text = R.string.user_database_error
        icon = R.drawable.ic_dns_white_24dp
        color = R.color.red_200
      }
      NetworkErrors.NETWORK_ERROR -> {
        text = R.string.network_error_desc
        icon = R.drawable.ic_cloud_off_white_24dp
        color = R.color.red_200
      }
      NetworkErrors.UNKNOWN_ERROR -> {
        text = R.string.unknown_error_desc
        icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        color = R.color.red_200
      }
      NetworkErrors.CLIENT_ERROR -> {
        text = R.string.unknown_error_desc
        icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        color = R.color.red_200
      }
      NetworkErrors.NOT_FOUND_ERROR -> TODO()
      NetworkErrors.UNAUTHORIZED_ERROR -> TODO()
    }

    customToast(
      resources.getString(text),
      color,
      icon
    )
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentCoinsListBinding.inflate(inflater, container, false)
    return binding
  }
}