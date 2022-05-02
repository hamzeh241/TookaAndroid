package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.MarketsAdapter
import ir.tdaapp.tooka.databinding.FragmentRelatedCoinsBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.util.getCurrentLocale
import ir.tdaapp.tooka.models.viewmodels.RelatedCoinsViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class RelatedCoinsFragment: BaseFragment(), CoroutineScope {

  private lateinit var binding: FragmentRelatedCoinsBinding

  private lateinit var adapter: MarketsAdapter
  private val viewModel: RelatedCoinsViewModel by inject()
  private val args: RelatedCoinsFragmentArgs by navArgs()

  init {
    EventBus.getDefault().register(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized)
      return binding.root

    binding = FragmentRelatedCoinsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.swipeRefreshLayout.isRefreshing = false
    initToolbar()
    initAdapter()
    initList()
    initObservables()
    initSwipeRefresh()

    lifecycleScope.launchWhenCreated {
      viewModel.getData(args.coinId)
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onPriceUpdate(livePriceListResponse: LivePriceListResponse) {
    launch {
      if (this@RelatedCoinsFragment::adapter.isInitialized)
        adapter.notifyChanges(livePriceListResponse)
    }
  }

  private fun initSwipeRefresh() = binding.swipeRefreshLayout.setOnRefreshListener {
    launch(Dispatchers.IO) {
      viewModel.getData(args.coinId, true)
    }
  }

  private fun initAdapter() {
    adapter = MarketsAdapter { clicked, position ->

      findNavController().navigate(
        RelatedCoinsFragmentDirections.actionRelatedCoinsFragmentToCoinDetailsFragment(
          clicked.id,
          "apikey",
          when (getCurrentLocale(requireContext())) {
            "fa" -> clicked.persianName ?: clicked.name
            else -> clicked.name
          }, clicked.icon
        )
      )

    }
  }

  fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = getString(R.string.other_coins)
  }

  fun initList() = with(binding.list) {
    layoutManager = LinearLayoutManager(requireContext())
    adapter = this@RelatedCoinsFragment.adapter
  }

  fun initObservables() {
    viewModel.coins.observe(viewLifecycleOwner, {
      binding.swipeRefreshLayout.isRefreshing = false
      adapter.differ.submitList(it as ArrayList)
    })

    viewModel.error.observe(viewLifecycleOwner) {
      binding.swipeRefreshLayout.isRefreshing = false
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

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("RelatedCoinsFragmentJob")

  override fun onDestroy() {
    super.onDestroy()
    EventBus.getDefault().unregister(this)
  }
}