package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.AlternateCoinsAdapter
import ir.tdaapp.tooka.models.adapters.ImportantNewsAdapter
import ir.tdaapp.tooka.models.adapters.TopCoinsAdapter
import ir.tdaapp.tooka.databinding.FragmentHomeBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.getCurrentLocale
import ir.tdaapp.tooka.models.viewmodels.HomeViewModel
import ir.tdaapp.tooka.models.viewmodels.SharedViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

  private val newsLayoutManager by lazy {
    GridLayoutManager(requireActivity(), 2, GridLayoutManager.HORIZONTAL, false)
  }
  private val topCoinsLayoutManager by lazy {
    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
  }
  private val gainersLosersManager by lazy {
    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
  }
  private val watchlistManager by lazy {
    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
  }

  private val viewModel: HomeViewModel by inject()
  private val sharedViewModel: SharedViewModel by inject()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + handler

  val handler = CoroutineExceptionHandler { _, exception ->
    Timber.i("Coroutine Exception: " + exception + " handled !")
  }

  init {
    EventBus.getDefault().register(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::homeBinding.isInitialized)
      return homeBinding.root
    else {
      homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
      return homeBinding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setHasOptionsMenu(true)
    initializeAdapters()
    initializeLists()
    initListeners()
    initScroll()
    initObservables()
    homeBinding.toolbar.title = getString(R.string.app_name_translatable)
    homeBinding.toolbar.setOnMenuItemClickListener(this)
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

  @Subscribe
  fun onPriceUpdate(livePrice: LivePriceListResponse) {
    launch {
      topCoinAdapter.notifyChanges(livePrice)
      gainersLosersAdapter.notifyChanges(livePrice)
      watchlistAdapter.notifyChanges(livePrice)
    }
  }

  fun initListeners() {
    homeBinding.includeHomeMisc.converter.setOnClickListener(this)
    homeBinding.includeHomeMisc.comparer.setOnClickListener(this)
    homeBinding.includeHomeMisc.alerts.setOnClickListener(this)
    homeBinding.includeWatchlist.addWatchlist.setOnClickListener(this)
  }

  fun initObservables() {
    viewModel.topCoinsList.observe(viewLifecycleOwner) {
      showTopList()
      topCoinAdapter.differ.submitList(it)
    }
    viewModel.breakingNewsList.observe(viewLifecycleOwner) {
      importantNewsAdapter.differ.submitList(it)
      showNewsList()
    }
    viewModel.gainersLosersList.observe(viewLifecycleOwner) {
      gainersLosersAdapter.differ.submitList(it)
      showGainersList()
    }
    viewModel.watchList.observe(viewLifecycleOwner) {
      if (it.size > 0) {
        watchlistAdapter.differ.submitList(it)
        showWatchlist()
      } else homeBinding.includeWatchlist.apply {
        noWatchlistRoot.visibility = View.VISIBLE
        watchlistList.visibility = View.GONE
        breakingNewsLoading.visibility = View.GONE
      }
    }
    viewModel.error.observe(viewLifecycleOwner, {
    })

    sharedViewModel.error.observe(viewLifecycleOwner, {

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

    }
    watchlistAdapter = AlternateCoinsAdapter { clicked, position ->

    }
    importantNewsAdapter = ImportantNewsAdapter { clicked, position ->

    }
  }

  private fun initializeLists() {
    homeBinding.includeImportantNews.importantNewsList.layoutManager = newsLayoutManager
    homeBinding.includeImportantNews.importantNewsList.adapter = importantNewsAdapter

    homeBinding.includeTopCoins.topCoinsList.layoutManager = topCoinsLayoutManager
    homeBinding.includeTopCoins.topCoinsList.adapter = topCoinAdapter

    homeBinding.includeGainersLosers.gainersLosersList.layoutManager = gainersLosersManager
    homeBinding.includeGainersLosers.gainersLosersList.adapter = gainersLosersAdapter

    homeBinding.includeWatchlist.watchlistList.layoutManager = watchlistManager
    homeBinding.includeWatchlist.watchlistList.adapter = watchlistAdapter
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
        findNavController().navigate(R.id.action_homeFragment_to_alertListFragment)
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

  private fun showTopList() {
    homeBinding.includeTopCoins.topCoinsList.visibility = View.VISIBLE
    homeBinding.includeTopCoins.topCoinsLoading.visibility = View.GONE
    homeBinding.includeTopCoins.topCoinsLoading.pauseAnimation()
  }

  private fun showNewsList() {
    homeBinding.includeImportantNews.importantNewsList.visibility = View.VISIBLE
    homeBinding.includeImportantNews.breakingNewsLoading.apply {
      visibility = View.GONE
      pauseAnimation()
    }
  }

  private fun showGainersList() {
    homeBinding.includeGainersLosers.gainersLosersList.visibility = View.VISIBLE
    homeBinding.includeGainersLosers.breakingNewsLoading.apply {
      visibility = View.GONE
      pauseAnimation()
    }
  }

  private fun showWatchlist() {
    homeBinding.includeWatchlist.watchlistList.visibility = View.VISIBLE
    homeBinding.includeWatchlist.breakingNewsLoading.apply {
      visibility = View.GONE
      pauseAnimation()
    }
  }
}