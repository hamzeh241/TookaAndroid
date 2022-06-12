package ir.tdaapp.tooka.ui.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentHomeBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.adapters.AlternateCoinsAdapter
import ir.tdaapp.tooka.models.adapters.ImportantNewsAdapter
import ir.tdaapp.tooka.models.adapters.TopCoinsAdapter
import ir.tdaapp.tooka.models.components.TookaSnackBar
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.dataclasses.News
import ir.tdaapp.tooka.models.util.*
import ir.tdaapp.tooka.models.viewmodels.HomeViewModel
import ir.tdaapp.tooka.ui.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class HomeFragment: BaseFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener,
  CoroutineScope {

  companion object {
    const val TAG = "HomeFragment"
  }

  private lateinit var homeBinding: FragmentHomeBinding

  private lateinit var topCoinAdapter: TopCoinsAdapter
  private lateinit var gainersLosersAdapter: AlternateCoinsAdapter
  private lateinit var watchlistAdapter: AlternateCoinsAdapter
  private lateinit var importantNewsAdapter: ImportantNewsAdapter

  private val viewModel: HomeViewModel by inject()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  private var isAlreadyCreated = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::homeBinding.isInitialized) {
      isAlreadyCreated = true
      return homeBinding.root
    } else {
      homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
      return homeBinding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setHasOptionsMenu(true)
    if (!isAlreadyCreated) {
      EventBus.getDefault().register(this)
      initSwipeRefresh()
      initializeAdapters()
      initializeLists()
      initListeners()
      initScroll()
      initObservables()
      homeBinding.toolbar.title = getString(R.string.app_name_translatable)
      homeBinding.toolbar.setOnMenuItemClickListener(this)
    }

    lifecycleScope.launchWhenResumed {
      viewModel.getData(getUserId())
    }
  }

  private fun initScroll() {
    homeBinding.homeScroll.viewTreeObserver.addOnScrollChangedListener(object:
      ViewTreeObserver.OnScrollChangedListener {
      override fun onScrollChanged() {
        when (homeBinding.homeScroll.scrollY > 0) {
          true -> homeBinding.toolbar.elevation = 8f
          else -> homeBinding.toolbar.elevation = 0f
        }
      }
    })
  }

  private fun initSwipeRefresh() = homeBinding.swipeRefreshLayout.setOnRefreshListener {
    launch {
      viewModel.getData(getUserId())
    }
  }

  @Subscribe
  fun onPriceUpdate(livePrice: LivePriceListResponse) {
    launch {
      topCoinAdapter.notifyChanges(livePrice)
      gainersLosersAdapter.notifyChanges(livePrice)
      watchlistAdapter.notifyChanges(livePrice)
    }
  }

  fun initListeners() = homeBinding.run {
    includeHomeMisc.converter.setOnClickListener(this@HomeFragment)
    includeHomeMisc.comparer.setOnClickListener(this@HomeFragment)
    includeHomeMisc.alerts.setOnClickListener(this@HomeFragment)
    includeWatchlist.addWatchlist.setOnClickListener(this@HomeFragment)
  }

  fun initObservables() {
    viewModel.data.observe(viewLifecycleOwner) {
      homeBinding.swipeRefreshLayout.isRefreshing = false
      launch(Dispatchers.Main) {
        showTopList()
        topCoinAdapter.differ.submitList(it.topCoins)
        delay(300)

        importantNewsAdapter.differ.submitList(it.breakingNews)
        showNewsList()
        delay(300)

        gainersLosersAdapter.differ.submitList(it.gainersLosers)
        showGainersList()

        if (it.watchlist.size > 0) {
          watchlistAdapter.differ.submitList(it.watchlist)
          showWatchlist()
        } else homeBinding.includeWatchlist.apply {
          val emptyView =
            emptyStateViewHorizontal(
              R.string.no_watchlist,
              lottieRawRes = R.raw.bitcoin
            )
          emptyView.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          watchlistSubRoot.addView(emptyView)
//          noWatchlistRoot.visibility = View.VISIBLE
          watchlistList.visibility = View.GONE
          breakingNewsLoading.visibility = View.GONE
        }
      }
    }
    viewModel.error.observe(viewLifecycleOwner, {
      homeBinding.swipeRefreshLayout.isRefreshing = false

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
    })
  }

  private fun initializeAdapters() {
    topCoinAdapter = TopCoinsAdapter { clicked, position ->
      findNavController().navigate(
        HomeFragmentDirections.actionHomeFragmentToCoinDetailsFragment(
          clicked.id,
          "asdasda",
          when (getCurrentLocale(requireContext())) {
            "fa" -> if (clicked.persianName != null) clicked.persianName!! else clicked.name
            else -> clicked.name
          },
          clicked.icon
        )
      )
    }
    gainersLosersAdapter = AlternateCoinsAdapter { clicked, position ->
      findNavController().navigate(
        HomeFragmentDirections.actionHomeFragmentToCoinDetailsFragment(
          clicked.id,
          "asdasda",
          when (getCurrentLocale(requireContext())) {
            "fa" -> if (clicked.persianName != null) clicked.persianName!! else clicked.name
            else -> clicked.name
          },
          clicked.icon
        )
      )
    }
    watchlistAdapter = AlternateCoinsAdapter { clicked, position ->
      navigate(
        HomeFragmentDirections.actionHomeFragmentToCoinDetailsFragment(
          clicked.id,
          "asdasda",
          when (getCurrentLocale(requireContext())) {
            "fa" -> if (clicked.persianName != null) clicked.persianName!! else clicked.name
            else -> clicked.name
          },
          clicked.icon
        )
      )
    }
    importantNewsAdapter = ImportantNewsAdapter { clicked, position ->
      newsClicked(clicked)
    }
  }

  private fun newsClicked(clicked: News) {
    when (clicked.newsKind) {
      News.EXTERNAL_NEWS -> {
        openWebpage(requireActivity(), clicked.url!!)
      }
      News.INTERNAL_NEWS -> {
        findNavController().navigate(
          HomeFragmentDirections.actionHomeFragmentToNewsDetailsFragment(
            clicked.id
          )
        )
      }
      News.SHORT_NEWS -> {
        NewsDetailsDialog(clicked.id).show(
          requireActivity().supportFragmentManager,
          NewsDetailsDialog.TAG
        )
      }
    }
  }

  private fun initializeLists() = homeBinding.run {
    includeImportantNews.importantNewsList.run {
      layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.HORIZONTAL, false)
      adapter = importantNewsAdapter
    }

    includeTopCoins.topCoinsList.run {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = topCoinAdapter
    }

    includeGainersLosers.gainersLosersList.run {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = gainersLosersAdapter
    }

    includeWatchlist.watchlistList.run {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = watchlistAdapter
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.converter -> {
        findNavController().navigate(R.id.action_homeFragment_to_converterFragment)
      }
      R.id.comparer -> {
        findNavController().navigate(R.id.action_homeFragment_to_compareFragment)
      }
      R.id.alerts -> {
        if ((requireActivity() as MainActivity).userPrefs.hasAccount())
          findNavController().navigate(R.id.action_homeFragment_to_alertListFragment)
        else {
          val colorOnError = getAttributeColor(homeBinding.root.context, R.attr.colorOnError)
          TookaSnackBar(
            homeBinding.root,
            getString(R.string.not_logged_in),
            Snackbar.LENGTH_LONG
          ).textConfig { textView ->
            textView.typeface = Typeface.createFromAsset(
              requireActivity().assets,
              "iranyekan_medium.ttf"
            )
            textView.setTextColor(colorOnError)
          }.backgroundConfig {
            it.setBackgroundColor(resources.getColor(R.color.red_200))
          }.show()
        }
      }
      R.id.addWatchlist -> {
        Timber.i("addWatchlist")
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    EventBus.getDefault().unregister(this)
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.search -> {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
      }
      R.id.notifications -> {
        findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
      }
    }
    return true
  }

  private fun showTopList() = homeBinding.includeTopCoins.run {
    topCoinsLoading.cancelAnimation()
    topCoinsLoading.visibility = View.GONE
  }

  private fun showNewsList() = homeBinding.includeImportantNews.run {
    breakingNewsLoading.apply {
      cancelAnimation()
      visibility = View.GONE
    }
  }

  private fun showGainersList() {
    homeBinding.includeGainersLosers.breakingNewsLoading.apply {
      cancelAnimation()
      visibility = View.GONE
    }
  }

  private fun showWatchlist() {
    homeBinding.includeWatchlist.breakingNewsLoading.apply {
      cancelAnimation()
      visibility = View.GONE
    }
    homeBinding.includeWatchlist.noWatchlistRoot.visibility = View.GONE
  }
}