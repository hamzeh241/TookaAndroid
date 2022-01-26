package ir.tdaapp.tooka.views.fragments

import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.AlternateCoinsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.ImportantNewsViewHolder
import ir.tdaapp.tooka.adapters.TopCoinViewHolder
import ir.tdaapp.tooka.databinding.*
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.viewmodels.HomeViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty

class HomeFragment: BaseFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener,
  CoroutineScope {

  private lateinit var homeBinding: FragmentHomeBinding

  private val topCoinsAdapter by lazy {
    object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TopCoinViewHolder(ItemSecondTopCoinBinding.inflate(layoutInflater, parent, false))
    }
  }

  private val importantNewsAdapter by lazy {
    object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImportantNewsViewHolder(
          ItemImportantNewsBinding.inflate(layoutInflater, parent, false)
        )
    }
  }
  private val gainersLosersAdapter by lazy {
    object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlternateCoinsViewHolder(
          ItemAlternateCoinsBinding.inflate(layoutInflater, parent, false)
        )
      }
    }
  }
  private val watchlistAdapter by lazy {
    object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlternateCoinsViewHolder(
          ItemAlternateCoinsBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
      }
    }
  }

  private val viewModel: HomeViewModel by inject()
  private val sharedViewModel: SharedViewModel by inject()

  private var isTopCoinsLoaded = false
    set(value) {
      if (value) initLivePrice()
      field = value
    }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + handler

  val handler = CoroutineExceptionHandler { _, exception ->
    Log.d("TAG", "Coroutine Exception: $exception handled !")
  }

  override fun init() {
    setHasOptionsMenu(true)
    initializeLists()

    homeBinding.toolbar.setOnMenuItemClickListener(this)
  }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    homeBinding.toolbar.title = getString(R.string.app_name_translatable)
  }

  private fun initLivePrice() = viewModel.subscribeToLivePrice()

  override fun initListeners(view: View) {
    topCoinsAdapter.callback = TookaAdapter.Callback { coin, models ->
      findNavController().navigate(
        HomeFragmentDirections.actionHomeToCoinDetailsFragment(
          coin.id,
          "asdasda",
          when (getCurrentLocale(requireContext())) {
            "fa" -> if (coin.persianName != null) coin.persianName as String else coin.name
            else -> coin.name
          },
          coin.icon
        )
      )
    }

    importantNewsAdapter.callback = TookaAdapter.Callback { vm, position -> }

    gainersLosersAdapter.callback = TookaAdapter.Callback { vm, position -> }

    watchlistAdapter.callback = TookaAdapter.Callback { vm, position -> }

    homeBinding.includeHomeMisc.converter.setOnClickListener(this)
    homeBinding.includeHomeMisc.compare.setOnClickListener(this)
  }

  override fun initObservables() {
    viewModel.topCoinsList.observe(viewLifecycleOwner, {
      if (it.size > 0) {

        launch(Dispatchers.IO) {
          topCoinsAdapter.models = it as ArrayList<Coin>
          withContext(Dispatchers.Main) {
            homeBinding.includeTopCoins.topCoinsList.visibility = View.VISIBLE
            homeBinding.includeTopCoins.topCoinsLoading.visibility = View.GONE
          }
          withContext(Dispatchers.Main) {
            homeBinding.includeTopCoins.topCoinsLoading.pauseAnimation()
          }
          isTopCoinsLoaded = true
        }
      }
    })

    viewModel.livePrice.observe(viewLifecycleOwner, {
      if (it != null) {
        launch(Dispatchers.IO) {
          topCoinsAdapter.models.singleOrNull { model ->
            model.id == it.id
          }.let { model ->
            model?.priceUSD = it.priceUSD
            val position = topCoinsAdapter.models.indexOf(model)

            withContext(Dispatchers.Main) {
              topCoinsAdapter.notifyItemChanged(position)
            }
          }
        }
      }
    })

    viewModel.breakingNewsList.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        launch(Dispatchers.IO) {
          importantNewsAdapter.models = it as ArrayList<News>
          withContext(Dispatchers.Main) {
            homeBinding.includeImportantNews.importantNewsList.visibility = View.VISIBLE
            homeBinding.includeImportantNews.breakingNewsLoading.visibility = View.GONE
          }
          withContext(Dispatchers.Main) {
            homeBinding.includeImportantNews.breakingNewsLoading.pauseAnimation()
          }
        }
      }
    })
    viewModel.gainersLosersList.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        launch(Dispatchers.IO) {
          gainersLosersAdapter.models = it as ArrayList<Coin>
          withContext(Dispatchers.Main) {
            homeBinding.includeGainersLosers.gainersLosersList.visibility = View.VISIBLE
            homeBinding.includeGainersLosers.breakingNewsLoading.visibility = View.GONE
          }
          withContext(Dispatchers.Main) {
            homeBinding.includeGainersLosers.breakingNewsLoading.pauseAnimation()
          }
        }
      }
    })
    viewModel.watchList.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        launch(Dispatchers.IO) {
          watchlistAdapter.models = it as ArrayList<Coin>
          withContext(Dispatchers.Main) {
            homeBinding.includeWatchlist.watchlistList.visibility = View.VISIBLE
            homeBinding.includeWatchlist.breakingNewsLoading.visibility = View.GONE
          }
          withContext(Dispatchers.Main) {
            homeBinding.includeWatchlist.breakingNewsLoading.pauseAnimation()
          }
        }
      }
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

  private fun initializeLists() {
    launch(Dispatchers.IO) {
      viewModel.getData()
    }

    homeBinding.includeImportantNews.importantNewsList.layoutManager = newsLayoutManager
    homeBinding.includeImportantNews.importantNewsList.adapter = importantNewsAdapter

    topCoinsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
    homeBinding.includeTopCoins.topCoinsList.layoutManager = topCoinsLayoutManager
    homeBinding.includeTopCoins.topCoinsList.adapter = topCoinsAdapter

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
}