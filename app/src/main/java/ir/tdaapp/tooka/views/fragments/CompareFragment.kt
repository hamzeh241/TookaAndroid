package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.TimeFramesViewHolder
import ir.tdaapp.tooka.databinding.FragmentCompareBinding
import ir.tdaapp.tooka.databinding.ItemTimeFrameBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.viewmodels.CompareViewModel
import ir.tdaapp.tooka.views.dialogs.CoinsListBottomSheetDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CompareFragment: BaseFragmentSecond(), View.OnClickListener, CoroutineScope {

  private lateinit var binding: FragmentCompareBinding
  private lateinit var coinsList: List<Coin>

  private val timeFrameAdapter by lazy {
    object: TookaAdapter<TimeFrameModel>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TimeFramesViewHolder(ItemTimeFrameBinding.inflate(layoutInflater, parent, false))
    }
  }
  private val timeFrameManager by lazy {
    GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
  }

  private val viewModel by lazy {
    val viewModelFactory = ViewModelFactory()
    ViewModelProvider(this, viewModelFactory).get(CompareViewModel::class.java)
  }

  private var firstCoinId = 0
  private var secondCoinId = 0
  private var selectedTimeFrame: TimeFrameModel? = null

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initTimeFramesList()

    pageLoading(true)

    lifecycleScope.launchWhenCreated {
      viewModel.getAllCoins()
    }
  }

  override fun initListeners(view: View) {
    timeFrameAdapter.callback = TookaAdapter.Callback { vm, position ->
      if (!timeFrameAdapter.models[position].isSelected) {
        val oldPosition = async {
          timeFrameAdapter.models.run {
            singleOrNull { it.isSelected }.let { timeModel ->
              indexOf(timeModel)
            }
          }
        }

        selectedTimeFrame = vm

        launch(Dispatchers.IO) {
          timeFrameAdapter.models.forEach { it.isSelected = false }

          timeFrameAdapter.models[position].isSelected = true

          withContext(Dispatchers.Main) {
            timeFrameAdapter.notifyItemChanged(oldPosition.await())
            timeFrameAdapter.notifyItemChanged(position)
          }
        }
        chartLoading(true)
        viewModel.getChartData(firstCoinId, secondCoinId, selectedTimeFrame!!.id)
      }

    }

    binding.firstCoin.setOnClickListener(this)
    binding.secondCoin.setOnClickListener(this)
  }

  override fun initObservables() {
    viewModel.coinsList.observe(viewLifecycleOwner, {
      coinsList = it
      setHeaderData(it[0], it[0])

      firstCoinId = it[0].id
      secondCoinId = it[0].id
    })

    viewModel.compareData.observe(viewLifecycleOwner) {
      if (it != null) {
        launch {

          if (timeFrameAdapter.models.isNullOrEmpty()) {
            timeFrameAdapter.models = it.time_frames as ArrayList<TimeFrameModel>
            timeFrameAdapter.models[0].isSelected = true
            selectedTimeFrame = timeFrameAdapter.models[0]

            withContext(Dispatchers.Main) {
              timeFrameAdapter.notifyItemChanged(0)
            }
          } else {
            timeFrameAdapter.models = it.time_frames as ArrayList<TimeFrameModel>

            val alreadySelectedPos =
              timeFrameAdapter.models.run {
                singleOrNull { it.id == selectedTimeFrame?.id }.let {
                  indexOf(it)
                }
              }

            timeFrameAdapter.models[alreadySelectedPos].isSelected = true
            withContext(Dispatchers.Main) {
              timeFrameAdapter.notifyItemChanged(alreadySelectedPos)
            }
          }

          withContext(Dispatchers.Main) {
            setStatData(it)
            chartLoading(true)
            pageLoading(false)
          }

          viewModel.getChartData(
            it.first_coin.stats.coin_id, it.second_coin.stats.coin_id,
            selectedTimeFrame!!.id
          )

        }
      }
    }

    viewModel.chartData.observe(viewLifecycleOwner, {
      if (it != null) {
        setCompareChartData(binding.chart, it)
        pageLoading(false)
        chartLoading(false)
      }
    })
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.firstCoin -> {
        val dialog = CoinsListBottomSheetDialog(coinsList as ArrayList<Coin>)
        dialog.callback = object: CoinsListBottomSheetDialog.OnCoinSelected {
          override fun onCoinSelected(id: Int) {
            firstCoinId = id
            pageLoading(true)
            viewModel.getData(firstCoinId, secondCoinId)
          }
        }
        dialog.show(requireActivity().supportFragmentManager, CoinsListBottomSheetDialog.TAG)
      }
      R.id.secondCoin -> {
        val dialog = CoinsListBottomSheetDialog(coinsList as ArrayList<Coin>)
        dialog.callback = object: CoinsListBottomSheetDialog.OnCoinSelected {
          override fun onCoinSelected(id: Int) {
            secondCoinId = id
            pageLoading(true)
            viewModel.getData(firstCoinId, secondCoinId)
          }
        }
        dialog.show(requireActivity().supportFragmentManager, CoinsListBottomSheetDialog.TAG)
      }
    }
  }

  override fun initErrors() = Unit

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentCompareBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onDestroy() {
    super.onDestroy()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  fun pageLoading(state: Boolean) {
    binding.firstCoin.visibility = if (!state) View.VISIBLE else View.INVISIBLE
    binding.secondCoin.visibility = if (!state) View.VISIBLE else View.INVISIBLE
    binding.nestedScrollView.visibility = if (!state) View.VISIBLE else View.GONE
  }

  fun chartLoading(state: Boolean) {
    binding.chart.visibility = if (!state) View.VISIBLE else View.INVISIBLE
    binding.breakingNewsLoading2.visibility = if (!state) View.GONE else View.VISIBLE
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("CompareFragmentJob")

  fun initTimeFramesList() =
    binding.timeFrameList.apply {
      layoutManager = timeFrameManager
      adapter = timeFrameAdapter
    }

  override fun initTransitions() = Unit

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    binding.toolbar.title = getString(R.string.compare)
  }

  fun setStatData(compareModel: CompareModel) {
    binding.txtCoin1Name.text = compareModel.first_coin.name
    binding.txtCoin2Name.text = compareModel.second_coin.name
    binding.txtCoin1Symbol.text = compareModel.first_coin.symbol
    binding.txtCoin2Symbol.text = compareModel.second_coin.symbol
    Glide.with(requireContext())
      .load(RetrofitClient.COIN_IMAGES + compareModel.first_coin.icon)
      .into(binding.imgCoin1)
    Glide.with(requireContext())
      .load(RetrofitClient.COIN_IMAGES + compareModel.second_coin.icon)
      .into(binding.imgCoin2)

    binding.includeStats.txtCoin1Name.text = (compareModel.first_coin.name)
    binding.includeStats.txtCoin2Name.text = (compareModel.second_coin.name)
    binding.includeStats.txtCoin1CircSupply.text =
      separatePrice(compareModel.first_coin.stats.stat_circulating_supply)
    binding.includeStats.txtCoin2CircSupply.text =
      separatePrice(compareModel.second_coin.stats.stat_circulating_supply)
    binding.includeStats.txtCoin1MarketCap.text =
      separatePrice(compareModel.first_coin.stats.stat_market_cap)
    binding.includeStats.txtCoin2MarketCap.text =
      separatePrice(compareModel.second_coin.stats.stat_market_cap)
    binding.includeStats.txtCoin1MaxSupply.text =
      separatePrice(compareModel.first_coin.stats.stat_max_supply.toFloat())
    binding.includeStats.txtCoin2MaxSupply.text =
      separatePrice(compareModel.second_coin.stats.stat_max_supply.toFloat())
    binding.includeStats.txtCoin1Rank.text =
      separatePrice(compareModel.first_coin.stats.stat_rank.toFloat())
    binding.includeStats.txtCoin2Rank.text =
      separatePrice(compareModel.second_coin.stats.stat_rank.toFloat())
    binding.includeStats.txtCoin1TotalSupply.text =
      separatePrice(compareModel.first_coin.stats.stat_total_supply.toFloat())
    binding.includeStats.txtCoin2TotalSupply.text =
      separatePrice(compareModel.second_coin.stats.stat_total_supply.toFloat())
    binding.includeStats.txtCoin1Vol24.text =
      separatePrice(compareModel.first_coin.stats.stat_24h_volume)
    binding.includeStats.txtCoin2Vol24.text =
      separatePrice(compareModel.second_coin.stats.stat_24h_volume)
    binding.includeStats.txtCoin1Price.text =
      separatePrice(compareModel.first_coin.price_dollar)
    binding.includeStats.txtCoin2Price.text =
      separatePrice(compareModel.second_coin.price_dollar)
    binding.includeStats.txtCoin1PriceTooman.text =
      separatePrice(compareModel.first_coin.price_toman)
    binding.includeStats.txtCoin2PriceTooman.text =
      separatePrice(compareModel.second_coin.price_toman)

  }

  fun setHeaderData(firstCoin: Coin, secondCoin: Coin) {
    binding.txtCoin1Name.text = when (getCurrentLocale(requireContext())) {
      "fa" -> {
        if (firstCoin.persianName != null)
          firstCoin.persianName
        else firstCoin.name
      }
      "en" -> firstCoin.name
      else -> firstCoin.name
    }
    binding.txtCoin2Name.text = when (getCurrentLocale(requireContext())) {
      "fa" -> {
        if (secondCoin.persianName != null)
          secondCoin.persianName
        else secondCoin.name
      }
      "en" -> secondCoin.name
      else -> secondCoin.name
    }
    binding.txtCoin1Symbol.text = firstCoin.symbol
    binding.txtCoin2Symbol.text = secondCoin.symbol

    val imageUrl1 = RetrofitClient.COIN_IMAGES + firstCoin.icon
    val imageUrl2 = RetrofitClient.COIN_IMAGES + secondCoin.icon
    Glide.with(requireContext())
      .load(imageUrl1)
      .into(binding.imgCoin1)
    Glide.with(requireContext())
      .load(imageUrl2)
      .into(binding.imgCoin2)
  }
}