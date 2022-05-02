package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.NotificationsViewHolder
import ir.tdaapp.tooka.models.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.FragmentNotificationBinding
import ir.tdaapp.tooka.databinding.ItemNotificationBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.viewmodels.NotificationsViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class NotificationFragment: BaseFragment(), CoroutineScope {

  private lateinit var binding: FragmentNotificationBinding

  private lateinit var adapter: TookaAdapter<Notification>
  private val viewModel: NotificationsViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentNotificationBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initToolbar()
    initAdapter()
    initList()
    initListeners()
    initObservables()

    launch {
      viewModel.getData((requireActivity() as MainActivity).userPrefs.getUserId(requireContext()))
    }
  }

  fun initAdapter() {
    adapter = object: TookaAdapter<Notification>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        NotificationsViewHolder(ItemNotificationBinding.inflate(layoutInflater, parent, false))
    }
  }

  fun initList() {
    binding.notifList.layoutManager = LinearLayoutManager(requireContext())
    binding.notifList.adapter = adapter
  }

  fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = getString(R.string.notifications)
  }

  fun initListeners() {
    adapter.callback = TookaAdapter.Callback { vm, pos -> }
  }

  fun initObservables() {
    viewModel.result.observe(viewLifecycleOwner) {
      if (it != null && it.isNotEmpty()) {
        adapter.models = it as ArrayList<Notification>
        binding.notifList.visibility = View.VISIBLE

        binding.notifLoading.visibility = View.GONE
      }
    }

    viewModel.error.observe(viewLifecycleOwner){
      val message: String
      @DrawableRes val icon: Int
      when (it) {
        NetworkErrors.NETWORK_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.CLIENT_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        NetworkErrors.NOT_FOUND_ERROR -> {
          message = getString(R.string.coin_not_found)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          requireActivity().onBackPressed()
        }
        NetworkErrors.SERVER_ERROR -> {
          message = getString(R.string.server_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNAUTHORIZED_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNKNOWN_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
      }

      Toast(requireContext()).apply {
        setDuration(Toast.LENGTH_LONG)
        setView(ToastLayoutBinding.inflate(layoutInflater).apply {
          this.message.text = message
          image.setImageResource(icon)
        }.root)
        show()
      }
    }
  }

  override fun onDestroy() {
    (requireActivity() as MainActivity).bottomNavVisibility = true
    super.onDestroy()
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("NotificationFragmentJob")
}