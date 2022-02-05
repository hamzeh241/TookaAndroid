package ir.tdaapp.tooka.views.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.*
import ir.tdaapp.tooka.databinding.*
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.viewmodels.HomeViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class HomeFragment: BaseFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener,
  CoroutineScope {

  private lateinit var homeBinding: FragmentHomeBinding

  private lateinit var topCoinAdapter: TopCoinsAdapter
  private lateinit var gainersLosersAdapter: AlternateCoinsAdapter
  private lateinit var watchlistAdapter: AlternateCoinsAdapter

  private val importantNewsAdapter by lazy {
    object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImportantNewsViewHolder(
          ItemImportantNewsBinding.inflate(layoutInflater, parent, false)
        )
    }
  }

  private lateinit var viewModel: HomeViewModel
  private val sharedViewModel: SharedViewModel by inject()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + handler

  val handler = CoroutineExceptionHandler { _, exception ->
    Log.d("TAG", "Coroutine Exception: $exception handled !")
  }

  override fun init() {
    setHasOptionsMenu(true)
    val viewModelFactory = ViewModelFactory()
    viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    initializeAdapters()
    initializeLists()

    lifecycleScope.launchWhenStarted {
      viewModel.getData()
    }

    homeBinding.toolbar.setOnMenuItemClickListener(this)
  }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    homeBinding.toolbar.title = getString(R.string.app_name_translatable)
  }

  override fun initListeners(view: View) {

    importantNewsAdapter.callback = TookaAdapter.Callback { vm, position -> }

    homeBinding.includeHomeMisc.converter.setOnClickListener(this)
    homeBinding.includeHomeMisc.compare.setOnClickListener(this)
  }

  @DelicateCoroutinesApi
  override fun initObservables() {
    viewModel.topCoinsList.observe(viewLifecycleOwner, {
      showTopList()
      topCoinAdapter.differ.submitList(it)
    })

    viewModel.livePrice.observe(viewLifecycleOwner, { livePrice ->
      launch(Dispatchers.IO) {
        topCoinAdapter.notifyChanges(livePrice)
        gainersLosersAdapter.notifyChanges(livePrice)
        watchlistAdapter.notifyChanges(livePrice)
      }
    })

    viewModel.breakingNewsList.observe(viewLifecycleOwner, {
      importantNewsAdapter.models = it as ArrayList<News>
      showNewsList()
    })
    viewModel.gainersLosersList.observe(viewLifecycleOwner, {
      gainersLosersAdapter.differ.submitList(it)
      showGainersList()
    })
    viewModel.watchList.observe(viewLifecycleOwner, {
      watchlistAdapter.differ.submitList(it)
      showWatchlist()
    })
  }

  val onRetry = object: MainActivity.OnToastCallback {
    override fun callback(v: View) {
      launch(Dispatchers.IO) {
        viewModel.getData()
      }
    }

    override fun cancel(v: View) {
    }
  }

  override fun initErrors() {
    viewModel.error.observe(viewLifecycleOwner, {
      when (it) {
        NetworkErrors.CLIENT_ERROR -> errorToast(
          Pair(
            getString(R.string.unknown_error_title),
            getString(R.string.unknown_error_desc)
          ),
          onRetry
        )
        NetworkErrors.NETWORK_ERROR -> errorToast(
          Pair(
            getString(R.string.network_error_title),
            getString(R.string.network_error_desc)
          ),
          onRetry
        )
        NetworkErrors.SERVER_ERROR -> errorToast(
          Pair(
            getString(R.string.server_error_title),
            getString(R.string.server_error_desc)
          ),
          onRetry
        )
        NetworkErrors.UNAUTHORIZED_ERROR -> {/* Will set this later */
        }
        NetworkErrors.UNKNOWN_ERROR -> errorToast(
          Pair(
            getString(R.string.unknown_error_title),
            getString(R.string.unknown_error_desc)
          ),
          onRetry
        )
      }
    })

    sharedViewModel.error.observe(viewLifecycleOwner, {
      (requireActivity() as MainActivity).errorToast(
        Pair(
          getString(R.string.network_error_title),
          getString(R.string.server_error_desc)
        ),
        onRetry
      )
    })
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
    return homeBinding
  }

  val newsLayoutManager by lazy {
    GridLayoutManager(requireActivity(), 2, GridLayoutManager.HORIZONTAL, false)
  }
  val topCoinsLayoutManager by lazy {
    LinearLayoutManager(requireContext())
  }
  val gainersLosersManager by lazy {
    LinearLayoutManager(requireContext())
  }
  val watchlistManager by lazy {
    LinearLayoutManager(requireContext())
  }

  private fun initializeAdapters() {
    topCoinAdapter = TopCoinsAdapter { clicked, position ->

    }
    gainersLosersAdapter = AlternateCoinsAdapter { clicked, position ->

    }
    watchlistAdapter = AlternateCoinsAdapter { clicked, position ->

    }
  }

  private fun initializeLists() {
    homeBinding.includeImportantNews.importantNewsList.layoutManager = newsLayoutManager
    homeBinding.includeImportantNews.importantNewsList.adapter = importantNewsAdapter

    topCoinsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
    homeBinding.includeTopCoins.topCoinsList.layoutManager = topCoinsLayoutManager
    homeBinding.includeTopCoins.topCoinsList.adapter = topCoinAdapter

    gainersLosersManager.orientation = LinearLayoutManager.HORIZONTAL
    homeBinding.includeGainersLosers.gainersLosersList.layoutManager = gainersLosersManager
    homeBinding.includeGainersLosers.gainersLosersList.adapter = gainersLosersAdapter

    watchlistManager.orientation = LinearLayoutManager.HORIZONTAL
    homeBinding.includeWatchlist.watchlistList.layoutManager = watchlistManager
    homeBinding.includeWatchlist.watchlistList.adapter = watchlistAdapter
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.converter -> {
        findNavController().navigate(R.id.action_home_to_converterFragment)
      }
      R.id.compare -> {
        findNavController().navigate(R.id.action_home_to_compareFragment)
      }
    }
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.search -> {
        findNavController().navigate(R.id.action_home_to_searchFragment)
      }
      R.id.notifications -> {
        findNavController().navigate(R.id.action_home_to_notificationFragment)
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