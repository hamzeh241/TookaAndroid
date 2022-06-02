package ir.tdaapp.tooka.ui.fragments

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
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentMarketsBinding
import ir.tdaapp.tooka.models.adapters.MarketsAdapter
import ir.tdaapp.tooka.models.adapters.SortAdapter
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.util.TookaSwipeCallback
import ir.tdaapp.tooka.models.util.VIEW_TYPE_GRID
import ir.tdaapp.tooka.models.util.VIEW_TYPE_LINEAR
import ir.tdaapp.tooka.models.util.getName
import ir.tdaapp.tooka.models.viewmodels.MarketsViewModel
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MarketsFragment: BaseFragment(), View.OnClickListener, CoroutineScope {

  companion object {
    const val TAG = "MarketsFragment"
  }

  init {
    EventBus.getDefault().register(this)
  }

  lateinit var binding: FragmentMarketsBinding

  lateinit var adapter: MarketsAdapter
  lateinit var sortAdapter: SortAdapter

  val verticalDecoration by lazy {
    val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    decor.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration))
    decor
  }

  val horizontalDecoration by lazy {
    val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
    decor.setDrawable(resources.getDrawable(R.drawable.divider_item_decoration))
    decor
  }

  private var listVisibility: Boolean
    get() = if (binding.includeMarketsCoinList.marketCoinsList.visibility == View.VISIBLE) true else false
    set(value) {
      if (value) {
        binding.includeMarketsCoinList.loading.apply {
          pauseAnimation()
          visibility = View.GONE
        }
        binding.includeMarketsCoinList.marketCoinsList.visibility = View.VISIBLE
      } else {
        binding.includeMarketsCoinList.loading.apply {
          playAnimation()
          visibility = View.VISIBLE
        }
        binding.includeMarketsCoinList.marketCoinsList.visibility = View.GONE
      }
    }

  private val viewModel by inject<MarketsViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized)
      return binding.root
    else {
      binding = FragmentMarketsBinding.inflate(inflater, container, false)
      return binding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initAdapters()
    initMarketCoinsList()
    initSwipeGesture()
    initSwipeRefreshLayout()
    initObservables()

    binding.toolbar.title = getString(R.string.markets)
  }

  override fun onStart() {
    super.onStart()
    if (viewModel.lastScrollPosition.value != null)
      binding.includeMarketsCoinList.marketCoinsList.scrollToPosition(viewModel.lastScrollPosition.value!!)
  }

  fun initObservables() {
    viewModel.coinsList.observe(viewLifecycleOwner) {
      listVisibility = true
      val isGrid = adapter.differ.currentList.any { it.viewType == VIEW_TYPE_GRID }
      it as ArrayList<Coin>
      if (isGrid) {
        it.forEach { coin -> coin.viewType = VIEW_TYPE_GRID }
      }

      adapter.differ.submitList(it)
      binding.swipeRefreshLayout.isRefreshing = false
    }

    viewModel.sortList.observe(viewLifecycleOwner) {
      sortAdapter.differ.submitList(it)
    }
    viewModel.selectedSort.observe(viewLifecycleOwner) {
      listVisibility = false
      launch {
        viewModel.getCoins(
          it.isAscend,
          it.id
        )
      }
    }
  }

  @Subscribe
  fun onPriceUpdate(response: LivePriceListResponse) {
    launch(Dispatchers.IO) {
      adapter.notifyChanges(response)
    }
  }

  private fun initSwipeRefreshLayout() {
    binding.swipeRefreshLayout.setOnRefreshListener {
      listVisibility = false

      launch {
        viewModel.getCoins(
          viewModel.selectedSort.value!!.isAscend,
          viewModel.selectedSort.value!!.id,
        )
      }
    }
  }

  private fun initSwipeGesture() {
    val callback = TookaSwipeCallback(ItemTouchHelper.LEFT, { viewHolder, direction ->

      val position = viewHolder.adapterPosition
      val coin = adapter.differ.currentList[position]
      coin.isWatchlist = true
      adapter.notifyItemChanged(position)

    }, { decorator ->

      decorator.apply {
        addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gold_300))
        addSwipeLeftActionIcon(R.drawable.ic_star_outline)
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

  private val linearManager by lazy {
    LinearLayoutManager(requireContext())
  }

  private val gridManager by lazy {
    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
  }

  private fun initMarketCoinsList() {
    binding.includeMarketsCoinList.marketCoinsList.apply {
      layoutManager = linearManager
      adapter = this@MarketsFragment.adapter
      addItemDecoration(verticalDecoration)
    }
  }

  private val sortManager by lazy {
    LinearLayoutManager(requireContext()).apply {
      orientation = LinearLayoutManager.HORIZONTAL
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.viewTypeLayout -> {
        if (viewModel.viewType.value!! == MarketsViewModel.ViewType.Linear) {
          viewModel.viewType.value = MarketsViewModel.ViewType.Grid
          binding.includeMarketsSort.imgToggle.setImageResource(R.drawable.ic_grid_view_black_24dp)
          binding.includeMarketsCoinList.marketCoinsList.apply {
            layoutManager = linearManager
            removeItemDecoration(horizontalDecoration)
          }
          launch(Dispatchers.Main) {
            listVisibility = false
            adapter.changeViewType(VIEW_TYPE_LINEAR)
            listVisibility = true
          }
        } else {
          viewModel.viewType.value = MarketsViewModel.ViewType.Linear
          binding.includeMarketsSort.imgToggle.setImageResource(R.drawable.ic_view_stream_black_24dp)
          binding.includeMarketsCoinList.marketCoinsList.apply {
            layoutManager = gridManager
            addItemDecoration(horizontalDecoration)
          }
          launch(Dispatchers.Main) {
            listVisibility = false
            adapter.changeViewType(VIEW_TYPE_GRID)
            listVisibility = true
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.lastScrollPosition.value = linearManager.findFirstCompletelyVisibleItemPosition()
    EventBus.getDefault().unregister(this)
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("MarketsFragmentJob")
}