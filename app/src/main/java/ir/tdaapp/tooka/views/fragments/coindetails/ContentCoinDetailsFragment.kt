package ir.tdaapp.tooka.views.fragments.coindetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.AlternateCoinsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.ImportantNewsViewHolder
import ir.tdaapp.tooka.adapters.TimeFramesViewHolder
import ir.tdaapp.tooka.databinding.FragmentContentCoinDetailsBinding
import ir.tdaapp.tooka.databinding.ItemAlternateCoinsBinding
import ir.tdaapp.tooka.databinding.ItemImportantNewsBinding
import ir.tdaapp.tooka.databinding.ItemTimeFrameBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.CoinDetailsModel
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.models.TimeFrameModel
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.viewmodels.CoinDetailsViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

import com.github.mikephil.charting.data.CandleEntry

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import ir.tdaapp.tooka.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import saman.zamani.persiandate.PersianDate
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ContentCoinDetailsFragment(val coinId: Int, val coinName: String): BaseFragment(),
  View.OnClickListener, CoinFragment.OnChartChange, CoroutineScope {

  lateinit var binding: FragmentContentCoinDetailsBinding

  lateinit var newsAdapter: TookaAdapter<News>
  lateinit var coinAdapter: TookaAdapter<Coin>
  lateinit var timeFrameAdapter: TookaAdapter<TimeFrameModel>

  var closeValues = arrayListOf<Double>()
  var isLinear = true
  var coin: CoinDetailsModel? = null
  val viewModel: CoinDetailsViewModel by inject()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

  override fun init() {
    initNewsList()
    initCoinsList()
    initTimeFramesList()

    launch(Dispatchers.IO) {
      viewModel.getData(coinId)
    }
  }


  override fun initTransitions() = Unit

  override fun initToolbar() = Unit

  override fun initListeners(view: View) {
    (parentFragment as CoinFragment).onChartChange = this

    binding.includeCoinChart.lineChart.setOnChartValueSelectedListener(object:
      OnChartValueSelectedListener {
      override fun onValueSelected(e: Entry?, h: Highlight?) {
        setLineData(e!!)
      }

      override fun onNothingSelected() {
        binding.includeCoinChart.cardCandleDetails.visibility = View.GONE
      }

    })
    binding.includeCoinChart.candleStickChart.setOnChartValueSelectedListener(object:
      OnChartValueSelectedListener {
      override fun onValueSelected(e: Entry?, h: Highlight?) {
        setCandleData(e as CandleEntry)
        val item = e as CandleEntry
      }

      override fun onNothingSelected() {
        binding.includeCoinChart.cardCandleDetails.visibility = View.GONE
        binding.includeCoinChart.candleDetails.visibility = View.GONE
        binding.includeCoinChart.coinDetails.visibility = View.VISIBLE
      }

    })

    newsAdapter.callback = TookaAdapter.Callback { vm, position -> }

    coinAdapter.callback = TookaAdapter.Callback { vm, position -> }

    timeFrameAdapter.callback = TookaAdapter.Callback { vm, position ->
      binding.includeCoinChart.cardCandleDetails.visibility = View.GONE
      if (!vm.isSelected) {
        var a = 0
        for (item in timeFrameAdapter.models) {
          if (item.isSelected) {
            item.isSelected = false
            break
          }
          a++
        }
        vm.isSelected = true
        timeFrameAdapter.notifyItemChanged(a)
        timeFrameAdapter.notifyItemChanged(position)
        chartLoading()
        viewModel.getChartData(coinId, vm.id)
      }
    }
    timeFrameAdapter.callback = TookaAdapter.Callback { vm, position -> }

    binding.includeCoinMisc.txtSeeMoreNews.setOnClickListener(this)
    binding.includeCoinMisc.txtSeeMoreCoins.setOnClickListener(this)
  }

  override fun initObservables() {
    viewModel.details.observe(viewLifecycleOwner, {
      if (it != null) {
        binding.contentCoinLoading.visibility = View.GONE
        binding.contentCoinNested.visibility = View.VISIBLE
        setData(it)
        coin = it

        it.timeFrames.get(0).isSelected = true
        timeFrameAdapter.notifyItemChanged(0)
        timeFrameAdapter.models = it.timeFrames as ArrayList<TimeFrameModel>
        binding.includeCoinChart.timeFramesList.visibility = View.VISIBLE

        binding.includeCoinChart.candleStickChart.fitScreen()
        binding.includeCoinChart.lineChart.fitScreen()

        viewModel.getChartData(coinId, it.timeFrames.get(0).id)
      }
    })
    viewModel.relatedNews.observe(viewLifecycleOwner, {
      binding.contentCoinLoading.visibility = View.GONE
      if (it.size > 0) {
        binding.includeCoinMisc.loading.pauseAnimation()
        binding.includeCoinMisc.loading.visibility = View.GONE
        newsAdapter.models = it as ArrayList<News>
        binding.includeCoinMisc.relatedNewsList.visibility = View.VISIBLE
      }
    })
    viewModel.otherCoins.observe(viewLifecycleOwner, {
      binding.contentCoinLoading.visibility = View.GONE
      if (it.size > 0) {
        binding.includeCoinMisc.coinLoading.pauseAnimation()
        binding.includeCoinMisc.coinLoading.visibility = View.GONE
        coinAdapter.models = it as ArrayList<Coin>
        binding.includeCoinMisc.relatedCoinsList.visibility = View.VISIBLE
      }
    })
    viewModel.chartData.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        when (isLinear) {
          true -> {
            binding.includeCoinChart.candleStickChart.visibility = View.GONE
            binding.includeCoinChart.breakingNewsLoading.visibility = View.GONE
            binding.includeCoinChart.lineChart.visibility = View.VISIBLE
            closeValues.clear()

            for (a in it) {
              closeValues.add(a[4] as Double)
            }

            setCoinDetailsChartData(binding.includeCoinChart.lineChart, closeValues)
          }
          false -> {
            binding.includeCoinChart.candleStickChart.visibility = View.VISIBLE
            binding.includeCoinChart.lineChart.visibility = View.GONE

            setCandleChart(binding.includeCoinChart.candleStickChart, coin!!, it)
          }
        }
      }
    })
  }

  val onRetry = object: MainActivity.OnToastCallback {
    override fun callback(v: View) {
      launch(Dispatchers.IO) {
        viewModel.getData(coinId)
      }
    }

    override fun cancel(v: View) {

    }
  }

  override fun initErrors() {
    viewModel.error.observe(viewLifecycleOwner, {
      when (it) {
        NetworkErrors.CLIENT_ERROR ->
          errorToast(
            Pair(
              getString(R.string.unknown_error_title),
              getString(R.string.unknown_error_desc)
            ),
            onRetry
          )
        NetworkErrors.NETWORK_ERROR -> {
          errorToast(
            Pair(
              getString(R.string.network_error_title),
              getString(R.string.network_error_desc)
            ),
            onRetry
          )
        }
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
  }

  var candleData: PersianDate? = null

  fun setCandleData(e: CandleEntry) {
    /*
    if (binding.includeCoinChart.cardCandleDetails.visibility != View.VISIBLE)
      binding.includeCoinChart.cardCandleDetails.visibility = View.VISIBLE
    binding.includeCoinChart.txtCandleDetails.text = StringBuilder()
      .append(toPersianNumbers("${getString(R.string.open)}: ${separatePrice(candleEntry.open)} "))
      .append(toPersianNumbers("${getString(R.string.high)}: ${separatePrice(candleEntry.high)} "))
      .append(toPersianNumbers("${getString(R.string.low)}: ${separatePrice(candleEntry.low)} "))
      .append(toPersianNumbers("${getString(R.string.close)}: ${separatePrice(candleEntry.close)}"))
      .toString()
     */
    binding.includeCoinChart.coinDetails.visibility = View.GONE
    binding.includeCoinChart.candleDetails.visibility = View.VISIBLE
    binding.includeCoinChart.txtCandleDetailsLow.text = e.low.toString()
    binding.includeCoinChart.txtCandleDetailsHigh.text = e.high.toString()
    binding.includeCoinChart.txtCandleDetailsOpen.text = e.open.toString()
    binding.includeCoinChart.txtCandleDetailsClose.text = e.close.toString()
    candleData = PersianDate((e.data as Double).toLong())
    binding.includeCoinChart.txtCandleDetailsDate.text = candleData?.toString()
  }

  fun setLineData(entry: Entry) {
    if (binding.includeCoinChart.cardCandleDetails.visibility != View.VISIBLE)
      binding.includeCoinChart.cardCandleDetails.visibility = View.VISIBLE
    binding.includeCoinChart.txtCandleDetails.text = StringBuilder()
      .append(toPersianNumbers("${getString(R.string.close)}: ${separatePrice(entry.y)}"))
      .toString()
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentContentCoinDetailsBinding.inflate(inflater, container, false)
    return binding
  }

  fun setData(model: CoinDetailsModel) {
    binding.includeCoinChart.txtCoinPriceTMN.text = separatePrice(model.priceTMN.toFloat())
    binding.includeCoinChart.txtCoinPriceUSD.text = separatePrice(model.priceUSD.toFloat())
    binding.includeCoinChart.txtCoinPercentage.text = separatePrice(model.percentage.toFloat())
    binding.includeCoinChart.txtCoinPercentage.setTextColor(if (model.percentage > 0) R.color.green_100 else R.color.red_200)


    Glide.with(this)
      .load(if (model.percentage > 0) R.drawable.ic_arrow_ascend else R.drawable.ic_arrow_descend)
      .into(binding.includeCoinChart.imgAscend)

    binding.includeCoinStats.txtMarketCap.text = if (model.stats.marketCap != "0"
      && model.stats.marketCap != "0.0"
    ) separatePrice(model.stats.marketCap.toFloat()) else "-"
    binding.includeCoinStats.txt24HVolume.text = if (model.stats.vol24 != "0"
      && model.stats.vol24 != "0.0"
    ) separatePrice(model.stats.vol24.toFloat()) else "-"
    binding.includeCoinStats.txtCircSupply.text = if (model.stats.circSupply != "0"
      && model.stats.circSupply != "0.0"
    ) separatePrice(model.stats.circSupply.toFloat()) else "-"
    binding.includeCoinStats.txtROI.text = if (model.stats.roi != "0"
      && model.stats.roi != "0.0"
    ) separatePrice(model.stats.roi.toFloat()) else "-"
    binding.includeCoinStats.txtMaxSupply.text = if (model.stats.maxSupply != "0"
      && model.stats.maxSupply != "0.0"
    ) separatePrice(model.stats.maxSupply.toFloat()) else "-"
    binding.includeCoinStats.txtRank.text =
      if (model.stats.rank != 0) model.stats.rank.toString() else "-"
    binding.includeCoinStats.txtTotalSupply.text = if (model.stats.totalSupply != "0"
      && model.stats.totalSupply != "0.0"
    ) separatePrice(model.stats.totalSupply.toFloat()) else "-"
  }

  fun initNewsList() {
    val layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
    newsAdapter = object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImportantNewsViewHolder(
          ItemImportantNewsBinding.inflate(layoutInflater, parent, false)
        )
    }

    binding.includeCoinMisc.relatedNewsList.setHasFixedSize(true)
    layoutManager.isAutoMeasureEnabled = true
    binding.includeCoinMisc.relatedNewsList.setNestedScrollingEnabled(true)
    binding.includeCoinMisc.relatedNewsList.layoutManager = layoutManager
    binding.includeCoinMisc.relatedNewsList.adapter = newsAdapter
  }

  fun initCoinsList() {
    val layoutManager = LinearLayoutManager(requireContext())
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL

    coinAdapter = object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AlternateCoinsViewHolder(
          ItemAlternateCoinsBinding.inflate(layoutInflater, parent, false)
        )
    }

    binding.includeCoinMisc.relatedCoinsList.layoutManager = layoutManager
    binding.includeCoinMisc.relatedCoinsList.adapter = coinAdapter
  }

  fun initTimeFramesList() {
    timeFrameAdapter = object: TookaAdapter<TimeFrameModel>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TimeFramesViewHolder(ItemTimeFrameBinding.inflate(layoutInflater, parent, false))
    }

    val timeFrameManager =
      GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
    binding.includeCoinChart.timeFramesList.layoutManager = timeFrameManager
    binding.includeCoinChart.timeFramesList.adapter = timeFrameAdapter
  }

  fun chartLoading() {
    binding.includeCoinChart.lineChart.visibility = View.GONE
    binding.includeCoinChart.candleStickChart.visibility = View.GONE
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.txtSeeMoreNews -> {
        val parentFragment = parentFragment as CoinFragment
        parentFragment.seeMoreNews()
      }
      R.id.txtSeeMoreCoins -> {
        val parentFragment = parentFragment as CoinFragment
        parentFragment.seeMoreCoins()
      }
    }
  }

  override fun onChanged(chartType: CoinFragment.ChartType) {
    binding.includeCoinChart.cardCandleDetails.visibility = View.GONE
    var selectedTimeFrame: TimeFrameModel? = null
    for (i in timeFrameAdapter.models) {
      if (i.isSelected) {
        selectedTimeFrame = i
        break
      }
    }
    when (chartType) {
      CoinFragment.ChartType.CANDLESTICK -> {
        isLinear = false
        chartLoading()
        viewModel.getChartData(coinId, selectedTimeFrame!!.id)
      }
      CoinFragment.ChartType.LINEAR -> {
        isLinear = true
        chartLoading()
        viewModel.getChartData(coinId, selectedTimeFrame!!.id)
      }
    }
  }
}