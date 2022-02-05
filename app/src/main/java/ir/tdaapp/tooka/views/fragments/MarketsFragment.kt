package ir.tdaapp.tooka.views.fragments

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.MarketCoinsViewHolder
import ir.tdaapp.tooka.adapters.MarketsAdapter
import ir.tdaapp.tooka.adapters.SortOptionsViewHolder
import ir.tdaapp.tooka.databinding.FragmentMarketsBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsGridBinding
import ir.tdaapp.tooka.databinding.ItemSortOptionBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.viewmodels.HomeViewModel
import ir.tdaapp.tooka.viewmodels.MarketsViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


class MarketsFragment: BaseFragment(), View.OnClickListener, CoroutineScope {

  lateinit var binding: FragmentMarketsBinding

  lateinit var adapter: MarketsAdapter
  lateinit var sortAdapter: TookaAdapter<SortModel>

  var selectedSort: SortModel? = null

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

  var listVisibility: Boolean
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

  private val viewModel: MarketsViewModel by lazy {
    val viewModelFactory = ViewModelFactory()
    ViewModelProvider(this, viewModelFactory).get(MarketsViewModel::class.java)
  }

  override fun init() {
    initAdapters()
    initMarketCoinsList()
    initSortList()
    initSwipeGesture()
    initSwipeRefreshLayout()

    lifecycleScope.launchWhenStarted {
      viewModel.getSortOptions()
    }
  }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    binding.toolbar.title = getString(R.string.markets)
  }

  override fun initListeners(view: View) {
    binding.includeMarketsSort.imgGridList.setOnClickListener(this)
    binding.includeMarketsSort.imgLinearList.setOnClickListener(this)
//    adapter.callback = coinListener
    sortAdapter.callback = sortListener
  }

  private val sortListener = TookaAdapter.Callback<SortModel> { vm, position ->
    if (vm.isSelected) {
      vm.isAscend = !vm.isAscend
      sortAdapter.notifyItemChanged(position)
      listVisibility = false
      StaticFields.selectedSortModel = vm
      viewModel.getData(vm.isAscend, vm.id)
    } else {
      val list = sortAdapter.models
      val selected = list.firstOrNull { it.isSelected }
      val selectedPosition = list.indexOf(selected)
      list.forEach { it.isSelected = false }
      vm.isSelected = true
      vm.isAscend = true
      StaticFields.selectedSortModel = vm
      sortAdapter.notifyItemChanged(position)
      sortAdapter.notifyItemChanged(selectedPosition)
      listVisibility = false
      viewModel.getData(vm.isAscend, vm.id)
    }
  }

  override fun initObservables() {
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

    viewModel.livePrice.observe(viewLifecycleOwner) {
      launch(Dispatchers.IO) {
        adapter.notifyChanges(it)
      }
    }
    viewModel.sortList.observe(viewLifecycleOwner) {
      if (!it.isNullOrEmpty()) {
        sortAdapter.models = it as ArrayList<SortModel>
        binding.includeMarketsSort.sortOptionList.visibility = View.VISIBLE

        if (StaticFields.selectedSortModel != null) {
          selectOtherSort(StaticFields.selectedSortModel)
          viewModel.getData(
            StaticFields.selectedSortModel.isAscend,
            StaticFields.selectedSortModel.id
          )
        } else {
          val sort = selectFirstSort()
          if (sort != null)
            viewModel.getData(sort.isAscend, sort.id)
          else viewModel.getData(false, 1)
        }
      }
    }
  }

  private fun initSwipeRefreshLayout() {
    binding.swipeRefreshLayout.setOnRefreshListener {
      listVisibility = false
//          initAdapters()
//          initMarketCoinsList()

      viewModel.getSortOptions()
    }
  }

  private fun initSwipeGesture() {
    val callback = TookaSwipeCallback(ItemTouchHelper.LEFT, { viewHolder, direction ->

      val position = viewHolder.adapterPosition
      val coin = adapter.differ.currentList[position]
      coin.isWatchlist = true
      adapter.notifyItemChanged(position)
      viewModel.addToWatchlist(2, coin.id)

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

  private fun selectFirstSort(): SortModel? {
    val list = sortAdapter.models
    val sortOption = list.singleOrNull { it.id == 1 }
    sortOption?.isAscend = false
    sortOption?.isSelected = true

    selectedSort = sortOption
    return sortOption
  }

  private fun selectOtherSort(sortModel: SortModel): SortModel? {
    val list = sortAdapter.models
    val sortOption = list.singleOrNull { it.id == sortModel.id }

    return sortOption
  }

  override fun initErrors() = Unit

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentMarketsBinding.inflate(inflater, container, false)
    return binding
  }

  private fun initAdapters() {
    adapter = MarketsAdapter { item, pos ->
      findNavController().navigate(
        MarketsFragmentDirections.actionMarketsToCoinDetailsFragment(
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

  private fun initSortList() {
    sortAdapter = object: TookaAdapter<SortModel>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SortOptionsViewHolder(ItemSortOptionBinding.inflate(layoutInflater, parent, false))
    }
    val sortManager = LinearLayoutManager(requireContext()).apply {
      orientation = LinearLayoutManager.HORIZONTAL
      reverseLayout = true
    }

    binding.includeMarketsSort.sortOptionList.apply {
      layoutManager = sortManager
      adapter = sortAdapter
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.imgGridList -> {
        binding.includeMarketsCoinList.marketCoinsList.apply {
          layoutManager = gridManager
          addItemDecoration(horizontalDecoration)
        }
        launch(Dispatchers.Main) {
          listVisibility = false
          adapter.changeViewType(VIEW_TYPE_GRID)
          listVisibility = true
        }
        binding.includeMarketsSort.imgGridList.setColorFilter(resources.getColor(R.color.black))
        binding.includeMarketsSort.imgLinearList.setColorFilter(resources.getColor(R.color.gray_400))
      }
      R.id.imgLinearList -> {
        binding.includeMarketsCoinList.marketCoinsList.apply {
          layoutManager = linearManager
          removeItemDecoration(horizontalDecoration)
        }
        launch(Dispatchers.Main) {
          listVisibility = false
          adapter.changeViewType(VIEW_TYPE_LINEAR)
          listVisibility = true
        }
        binding.includeMarketsSort.imgGridList.setColorFilter(resources.getColor(R.color.gray_400))
        binding.includeMarketsSort.imgLinearList.setColorFilter(resources.getColor(R.color.black))
      }
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("MarketsFragmentJob")
}
