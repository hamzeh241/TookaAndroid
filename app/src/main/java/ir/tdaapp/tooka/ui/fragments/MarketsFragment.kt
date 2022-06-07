package ir.tdaapp.tooka.ui.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentMarketsBinding
import ir.tdaapp.tooka.models.adapters.MarketsAdapter
import ir.tdaapp.tooka.models.components.TookaSnackBar
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.util.TookaSwipeCallback
import ir.tdaapp.tooka.models.util.VIEW_TYPE_GRID
import ir.tdaapp.tooka.models.util.VIEW_TYPE_LINEAR
import ir.tdaapp.tooka.models.util.getName
import ir.tdaapp.tooka.models.viewmodels.MarketsViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MarketsFragment: BaseFragment(), View.OnClickListener, CoroutineScope {

  companion object {
    const val TAG = "MarketsFragment"
  }

  lateinit var binding: FragmentMarketsBinding
  lateinit var adapter: MarketsAdapter

  private val viewModel by inject<MarketsViewModel>()
  private var isAlreadyCreated = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized) {
      isAlreadyCreated = true
      return binding.root
    } else {
      binding = FragmentMarketsBinding.inflate(inflater, container, false)
      return binding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    if (!isAlreadyCreated) {
      EventBus.getDefault().register(this)
      initAdapters()
      initMarketCoinsList()
      initSwipeGesture()
      initSwipeRefreshLayout()
      initObservables()
    }

    binding.toolbar.title = getString(R.string.markets)
  }

  fun initObservables() {
    viewModel.coinsList.observe(viewLifecycleOwner) {
      Timber.i("observe coin list")
      binding.swipeRefreshLayout.isRefreshing = false
      val isGrid = adapter.differ.currentList.any { it.viewType == VIEW_TYPE_GRID }
      if (isGrid) {
        it.forEach { coin -> coin.viewType = VIEW_TYPE_GRID }
      }

      adapter.differ.submitList(it)
    }

    viewModel.selectedSort.observe(viewLifecycleOwner) {
      Timber.i("observe sort list")
      binding.swipeRefreshLayout.isRefreshing = true
      launch {
        viewModel.getCoins(
          it.isAscend,
          it.id,
          getUserId()
        )
      }
    }

    viewModel.watchList.observe(viewLifecycleOwner) { result ->
      var position = 0
      adapter.differ.currentList.forEachIndexed { index, coin ->
        if (coin.id == result.coinId) {
          position = index
        }
      }
      if (!result.status) {
        adapter.differ.currentList[position].isWatchlist = false
        adapter.notifyItemChanged(position)
        TookaSnackBar(
          binding.root,
          getString(R.string.not_logged_in),
          Snackbar.LENGTH_LONG
        ).textConfig {
          it.typeface = Typeface.createFromAsset(
            requireActivity().assets,
            "iranyekan_medium.ttf"
          )
        }.backgroundConfig {
          it.setBackgroundResource(R.drawable.error_snackbar_background)
        }.show()
      }
    }
  }

  @Subscribe
  fun onPriceUpdate(response: LivePriceListResponse) {
    launch(Dispatchers.IO) {
      adapter.notifyChanges(response)
    }
  }

  private fun initSwipeRefreshLayout() =
    binding.swipeRefreshLayout.setOnRefreshListener {
      launch {
        viewModel.getCoins(
          viewModel.selectedSort.value!!.isAscend,
          viewModel.selectedSort.value!!.id,
          getUserId(),
          refresh = true
        )
      }
    }


  private fun initSwipeGesture() {
    val callback = TookaSwipeCallback(ItemTouchHelper.LEFT, { viewHolder, direction ->

      val userId = getUserId()
      val position = viewHolder.absoluteAdapterPosition
      if (userId > 0)
        launch {
          viewModel.addWatchlist(
            getUserId(),
            this@MarketsFragment.adapter.differ.currentList[position].id
          )

          withContext(Dispatchers.Main) {
            adapter.differ.currentList[position].isWatchlist = true
            adapter.notifyItemChanged(position)
          }
        }
      else {
        TookaSnackBar(
          binding.root,
          getString(R.string.not_logged_in),
          Snackbar.LENGTH_LONG
        ).textConfig {
          it.typeface = Typeface.createFromAsset(
            requireActivity().assets,
            "iranyekan_medium.ttf"
          )
        }.backgroundConfig {
          it.setBackgroundResource(R.drawable.error_snackbar_background)
        }.show()
      }

    }, { decorator ->

      decorator.apply {
        addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gold_300))
        addSwipeLeftActionIcon(R.drawable.ic_visibility)
        create().decorate()
      }
    })
    val touchHelper = ItemTouchHelper(callback)
    touchHelper.attachToRecyclerView(binding.includeMarketsCoinList.marketCoinsList)
  }

  private fun initAdapters() {
    adapter = MarketsAdapter { item, pos ->
      Timber.i("initAdapters: ")
      findNavController().navigate(
        MarketsFragmentDirections.actionMarketsFragmentToCoinDetailsFragment(
          item.id,
          "asdasd",
          getName(requireContext(), item),
          item.icon
        )
      )
    }
  }

  private fun initMarketCoinsList() {
    val decor =
      DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    decor.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration))

    binding.includeMarketsCoinList.marketCoinsList.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = this@MarketsFragment.adapter
      addItemDecoration(decor)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.viewTypeLayout -> {
        val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
        decor.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration))
        if (viewModel.viewType.value!! == MarketsViewModel.ViewType.Linear) {
          viewModel.viewType.value = MarketsViewModel.ViewType.Grid
          binding.includeMarketsSort.imgToggle.setImageResource(R.drawable.ic_grid_view_black_24dp)
          binding.includeMarketsCoinList.marketCoinsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            removeItemDecoration(decor)
          }
          launch {
            adapter.changeViewType(VIEW_TYPE_LINEAR)
          }
        } else {
          viewModel.viewType.value = MarketsViewModel.ViewType.Linear
          binding.includeMarketsSort.imgToggle.setImageResource(R.drawable.ic_view_stream_black_24dp)
          binding.includeMarketsCoinList.marketCoinsList.apply {
            layoutManager =
              GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(decor)
          }
          launch {
            adapter.changeViewType(VIEW_TYPE_GRID)
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    EventBus.getDefault().unregister(this)
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("MarketsFragmentJob")
}
