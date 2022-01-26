package ir.tdaapp.tooka.views.fragments

import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.MarketCoinsViewHolder
import ir.tdaapp.tooka.adapters.SortOptionsViewHolder
import ir.tdaapp.tooka.databinding.FragmentMarketsBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsGridBinding
import ir.tdaapp.tooka.databinding.ItemSortOptionBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.viewmodels.MarketsViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.android.ext.android.inject
import java.lang.Exception


class MarketsFragment: BaseFragment(), View.OnClickListener {

  lateinit var binding: FragmentMarketsBinding

  lateinit var adapter: TookaAdapter<Coin>
  lateinit var sortAdapter: TookaAdapter<SortModel>
  lateinit var linearManager: LinearLayoutManager
  lateinit var gridManager: GridLayoutManager

  var selectedSort: SortModel? = null

  var listVisibility: Boolean
    get() = if (binding.includeMarketsCoinList.marketCoinsList.visibility == View.VISIBLE) true else false
    set(value) {
      if (value) {
        binding.includeMarketsCoinList.loading.pauseAnimation()
        binding.includeMarketsCoinList.loading.visibility = View.GONE
        binding.includeMarketsCoinList.marketCoinsList.visibility = View.VISIBLE
      } else {
        binding.includeMarketsCoinList.loading.playAnimation()
        binding.includeMarketsCoinList.loading.visibility = View.VISIBLE
        binding.includeMarketsCoinList.marketCoinsList.visibility = View.GONE
      }
    }

  val viewModel: MarketsViewModel by inject()
  private val sharedViewModel: SharedViewModel by inject()

  override fun init() {
    initMarketCoinsList()
    initSortList()
    initSwipeGesture()

    sharedViewModel.error.observe(viewLifecycleOwner, {
      Log.i("TAG", "markets: $it")
    })

    viewModel.getSortOptions()
  }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    binding.toolbar.title = getString(R.string.markets)
  }

  override fun initListeners(view: View) {
    binding.includeMarketsSort.imgGridList.setOnClickListener(this)
    binding.includeMarketsSort.imgLinearList.setOnClickListener(this)
    adapter.callback = coinListener
    sortAdapter.callback = sortListener
  }

  private val coinListener = TookaAdapter.Callback<Coin> { vm, position ->
    findNavController().navigate(
      MarketsFragmentDirections.actionMarketsToCoinDetailsFragment(
        vm.id,
        "asdasd",
        when (getCurrentLocale(requireContext())) {
          "fa" -> {
            if (vm.persianName != null)
              vm.persianName!!
            else vm.name
          }
          else -> vm.name
        },
        vm.icon
      )
    )
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
    viewModel.coinsList.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        listVisibility = true
        adapter.models = it as ArrayList<Coin>
        adapter.notifyDataSetChanged()
      }
    })
    viewModel.sortList.observe(viewLifecycleOwner, {
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
    })
    binding.includeMarketsCoinList.marketCoinsList.layoutManager = linearManager
    binding.includeMarketsCoinList.marketCoinsList.adapter = adapter
  }

  private fun initSwipeGesture() {
    val callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
      override fun onMove(
        p0: RecyclerView,
        p1: RecyclerView.ViewHolder,
        p2: RecyclerView.ViewHolder
      ): Boolean = false


      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val coin = adapter.models[position]
        coin.isWatchlist = true

        adapter.notifyItemChanged(position)
//        viewModel.addToWatchlist(2, coin.id)
      }

      override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        RecyclerViewSwipeDecorator.Builder(
          c,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )
          .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gold_300))
          .addSwipeLeftActionIcon(R.drawable.ic_star_outline)
          .create()
          .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
    }
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

  private fun initMarketCoinsList() {
    adapter = object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_LINEAR)
          return MarketCoinsViewHolder(
            ItemMarketCoinsFlatBinding.inflate(
              LayoutInflater.from(context),
              parent,
              false
            )
          )
        else return MarketCoinsViewHolder(
          ItemMarketCoinsGridBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
          )
        )
      }
    }

    linearManager = LinearLayoutManager(requireContext())
    gridManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
  }

  private fun initSortList() {
    sortAdapter = object: TookaAdapter<SortModel>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SortOptionsViewHolder(ItemSortOptionBinding.inflate(layoutInflater, parent, false))
    }
    val sortManager = LinearLayoutManager(requireContext())
    sortManager.orientation = LinearLayoutManager.HORIZONTAL
    sortManager.reverseLayout = true

    binding.includeMarketsSort.sortOptionList.layoutManager = sortManager
    binding.includeMarketsSort.sortOptionList.adapter = sortAdapter
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.imgGridList -> {
        binding.includeMarketsCoinList.marketCoinsList.layoutManager = gridManager
        adapter.changeViewType(VIEW_TYPE_GRID)
        binding.includeMarketsSort.imgGridList.setColorFilter(resources.getColor(R.color.black))
        binding.includeMarketsSort.imgLinearList.setColorFilter(resources.getColor(R.color.gray_400))
      }
      R.id.imgLinearList -> {
        binding.includeMarketsCoinList.marketCoinsList.layoutManager = linearManager
        adapter.changeViewType(VIEW_TYPE_LINEAR)
        binding.includeMarketsSort.imgGridList.setColorFilter(resources.getColor(R.color.gray_400))
        binding.includeMarketsSort.imgLinearList.setColorFilter(resources.getColor(R.color.black))
      }
    }
  }
}
