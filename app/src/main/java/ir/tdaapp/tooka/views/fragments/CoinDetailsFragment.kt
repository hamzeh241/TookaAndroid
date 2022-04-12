package ir.tdaapp.tooka.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.AlternateCoinsAdapter
import ir.tdaapp.tooka.adapters.ImportantNewsAdapter
import ir.tdaapp.tooka.adapters.TimeFramesAdapter
import ir.tdaapp.tooka.databinding.FragmentCoinDetailsFinalBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.CandleUpdateModel
import ir.tdaapp.tooka.models.CoinDetailsModel
import ir.tdaapp.tooka.models.LivePriceListResponse
import ir.tdaapp.tooka.models.TimeFrameModel
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.ChartType.CANDLESTICK
import ir.tdaapp.tooka.util.ChartType.LINEAR
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.viewmodels.CoinDetailsViewModel
import ir.tdaapp.tooka.viewmodels.MainActivityViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class CoinDetailsFragment: BaseFragment(), View.OnClickListener, CoroutineScope {

  companion object {
    const val TAG = "CoinDetailsFragment"

    private const val CANDLE_VISIBLE_RANGE = 90f
    private const val LINE_VISIBLE_RANGE = 35f
    private const val TETHER_ID = 205
    private const val RANDOM_COINS_COUNT = 5
  }

  init {
    EventBus.getDefault().register(this)
  }

  private lateinit var binding: FragmentCoinDetailsFinalBinding

  private lateinit var coinAdapter: AlternateCoinsAdapter
  private lateinit var timeFrameAdapter: TimeFramesAdapter
  private lateinit var newsAdapter: ImportantNewsAdapter

  private val args: CoinDetailsFragmentArgs by navArgs()
  private val viewModel: CoinDetailsViewModel by inject()
  private val activityViewModel: MainActivityViewModel by inject()

  private var detailVisibility = false
    set(value) {
      if (value) {
        binding.contentCoinLoading.visibility = View.GONE
        binding.scrollCoinDetails.visibility = View.VISIBLE
        binding.includeCoinChart.timeFramesList.visibility = View.VISIBLE
      } else {
        binding.contentCoinLoading.visibility = View.VISIBLE
        binding.scrollCoinDetails.visibility = View.GONE
        binding.includeCoinChart.timeFramesList.visibility = View.GONE
      }

      field = value
    }

  private var chartType = LINEAR
    set(value) {
      with(binding.includeCoinChart) {
        when (value) {
          CANDLESTICK -> {
            candleStickChart.visibility = View.VISIBLE
            lineChart.visibility = View.GONE
            breakingNewsLoading.visibility = View.GONE
          }
          LINEAR -> {
            lineChart.visibility = View.VISIBLE
            candleStickChart.visibility = View.GONE
            breakingNewsLoading.visibility = View.GONE
          }
        }
      }
      field = value
    }

  private var chartLoading = true
    set(value) {
      with(binding.includeCoinChart) {
        if (value) {
          candleStickChart.visibility = View.GONE
          lineChart.visibility = View.GONE
          breakingNewsLoading.visibility = View.VISIBLE
        } else {
          when (chartType) {
            CANDLESTICK -> {
              candleStickChart.visibility = View.VISIBLE
              lineChart.visibility = View.GONE
              breakingNewsLoading.visibility = View.GONE
            }
            LINEAR -> {
              candleStickChart.visibility = View.GONE
              lineChart.visibility = View.VISIBLE
              breakingNewsLoading.visibility = View.GONE
            }
          }
        }
      }

      field = value
    }

  private val newsManager by lazy {
    GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
  }
  private val coinManager by lazy {
    val layoutManager = LinearLayoutManager(requireContext())
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    layoutManager
  }
  private val timeFrameManager by lazy {
    GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
  }

  private var closeValues = arrayListOf<Double>()
  private var coin: CoinDetailsModel? = null
  private var isLoaded = false
  private var selectedTimeFrame: TimeFrameModel? = null
  private var candleData: PersianDate? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized)
      return binding.root
    else {
      binding = FragmentCoinDetailsFinalBinding.inflate(inflater, container, false)
      return binding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as MainActivity).bottomNavVisibility = false

    lifecycleScope.launchWhenCreated {
      activityViewModel.subscribeToCandleUpdate()
    }

    initToolbar()
    initAdapters()
    initCoinsList()
    initNewsList()
    initTimeFramesList()
    initListeners()
    requestForData()

    initObservables()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onPriceUpdate(response: LivePriceListResponse) {
    if (isLoaded) {
      if (coin?.stats!!.id == response.id) {
        binding.includeCoinChart.txtCoinPriceUSD.text =
          formatPrice(
            toPersianNumbers(separatePrice(response.priceUSD)),
            currency = getString(R.string.dollars)
          )
        binding.includeCoinChart.txtCoinPriceTMN.text =
          formatPrice(
            toPersianNumbers(separatePrice(response.priceTMN)),
            currency = getString(R.string.toomans)
          )
        animateChanges(coin!!, response)
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onCandleUpdate(candleList: List<CandleUpdateModel>) {
    val updateJob = launch(Dispatchers.IO) {
      if (coin != null) {
        if (chartType == CANDLESTICK) {
          if (candleList.any { it.coinId == coin?.stats!!.id }) {
            val updatedCandle = candleList.singleOrNull { it.timeFrameId == selectedTimeFrame?.id }
            if (updatedCandle != null) {
              val dataset = binding.includeCoinChart.candleStickChart.candleData.dataSets[0]
              val lastCandle = dataset.getEntryForIndex(dataset.entryCount - 1)
              if ((lastCandle.data as Number).toLong() == updatedCandle.openTime) {
                with(dataset.getEntryForIndex(dataset.entryCount - 1)) {
                  open = updatedCandle.openValue.toFloat()
                  close = updatedCandle.closeValue.toFloat()
                  high = updatedCandle.highValue.toFloat()
                  low = updatedCandle.lowValue.toFloat()
                }
              } else if ((lastCandle.data as Number).toLong() < updatedCandle.openTime) {
                val newCandle = CandleEntry(
                  dataset.entryCount.toFloat(),
                  updatedCandle.highValue.toFloat(),
                  updatedCandle.lowValue.toFloat(),
                  updatedCandle.openValue.toFloat(),
                  updatedCandle.closeValue.toFloat()
                )
                newCandle.data = updatedCandle.openTime
                Timber.i("new candle - ${newCandle.close}")
                dataset.addEntry(newCandle)
              }
            }
          }
        }
      }
    }

    updateJob.invokeOnCompletion {
      Timber.i("Job finished - error? ${it == null}")
      if (chartType == CANDLESTICK)
        if (it == null)
          binding.includeCoinChart.candleStickChart.invalidate()
    }
  }

  private fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    binding.title.text = args.coinName

    val imageUrl =
      RetrofitClient.COIN_IMAGES + args.coinIcon
    Glide.with(this)
      .load(imageUrl)
      .into(binding.icon)
  }

  private fun initListeners() {
    with(binding.includeCoinChart) {

      lineChart.addListener(onNothingSelected = {
        cardCandleDetails.visibility = View.GONE
      }, { entry, highlight ->
        setLineData(entry!!)
      })

      candleStickChart.addListener(onNothingSelected = {
        cardCandleDetails.visibility = View.GONE
        candleDetails.visibility = View.GONE
        priceDetails.visibility = View.VISIBLE
      }, { entry, _ ->
        setCandleData(entry as CandleEntry)
      })
      txtIrt.setOnClickListener(this@CoinDetailsFragment)
      txtUsd.setOnClickListener(this@CoinDetailsFragment)
    }

    with(binding.includeCoinMisc) {
      txtSeeMoreNews.setOnClickListener(this@CoinDetailsFragment)
      txtSeeMoreCoins.setOnClickListener(this@CoinDetailsFragment)
    }

    binding.imgToggleChart.setOnClickListener(this)
  }

  private fun initAdapters() {
    timeFrameAdapter = TimeFramesAdapter { clicked, position ->

      if (clicked.isSelected)
        return@TimeFramesAdapter

      chartLoading = true
      binding.includeCoinChart.timeFramesList.isEnabled = false

      val deselctorJob = launch(Dispatchers.IO) {
        for (item in timeFrameAdapter.differ.currentList)
          item.isSelected = false
      }

      deselctorJob.invokeOnCompletion {
        clicked.isSelected = true
        selectedTimeFrame = clicked
        launch {
          if (it == null) {
            withContext(Dispatchers.Main) {
              timeFrameAdapter.notifyDataSetChanged()
            }
          }
        }
      }

      launch {
        if (!StaticFields.isIrtSelected)
          viewModel.getChartData(args.coinId, clicked.id)
        else viewModel.getToomanChartData(args.coinId, clicked.id)
      }
    }
    newsAdapter = ImportantNewsAdapter { clicked, position ->

    }
    coinAdapter = AlternateCoinsAdapter { clicked, position ->
      findNavController().navigate(
        CoinDetailsFragmentDirections.actionCoinDetailsFragmentSelf(
          clicked.id,
          args.apiKey,
          when (getCurrentLocale(requireContext())) {
            "fa" -> clicked.persianName ?: clicked.name
            else -> clicked.name
          },
          clicked.icon
        )
      )
    }
  }

  private fun requestForData() = viewModel.getData(args.coinId, RANDOM_COINS_COUNT)

  private fun initObservables() {
    viewModel.details.observe(viewLifecycleOwner) {
      detailVisibility = true
      setData(it)
      coin = it
      isLoaded = true

      binding.includeCoinChart.candleStickChart.fitScreen()
      binding.includeCoinChart.lineChart.fitScreen()
    }
    viewModel.timeframes.observe(viewLifecycleOwner) {
      binding.includeCoinChart.timeFramesList.visibility = View.VISIBLE
      it[0].isSelected = true
      selectedTimeFrame = it[0]
      timeFrameAdapter.differ.submitList(it)
      launch {
        if (!StaticFields.isIrtSelected)
          viewModel.getChartData(args.coinId, it[0].id)
        else viewModel.getToomanChartData(args.coinId, it[0].id)
      }
    }
    viewModel.relatedNews.observe(viewLifecycleOwner) {
      binding.contentCoinLoading.visibility = View.GONE
      if (it.size > 0) {
        binding.includeCoinMisc.loading.apply {
          pauseAnimation()
          visibility = View.GONE
        }
        newsAdapter.differ.submitList(it)
        binding.includeCoinMisc.relatedNewsList.visibility = View.VISIBLE
      }
    }
    viewModel.otherCoins.observe(viewLifecycleOwner) {
      binding.contentCoinLoading.visibility = View.GONE
      binding.includeCoinMisc.coinLoading.pauseAnimation()
      binding.includeCoinMisc.coinLoading.visibility = View.GONE
      if (it.size > 0) {
        coinAdapter.differ.submitList(it)
        binding.includeCoinMisc.relatedCoinsList.visibility = View.VISIBLE
      }
    }
    viewModel.chartData.observe(viewLifecycleOwner) {
      Timber.i("initObservables: ")
      binding.includeCoinChart.timeFramesList.isEnabled = true
      chartLoading = false
      when (chartType) {
        CANDLESTICK -> {
          setCandleChart(binding.includeCoinChart.candleStickChart, coin!!, it)
//          binding.includeCoinChart.candleStickChart.apply {
//            setVisibleXRange(
//              candleData.entryCount.toFloat(),
//              CANDLE_VISIBLE_RANGE
//            )
//            moveViewToX(candleData.entryCount.toFloat())
//          }
        }
        LINEAR -> {
          closeValues.clear()

          for (a in it) {
            closeValues.add(a[4] as Double)
          }

          setCoinDetailsChartData(binding.includeCoinChart.lineChart, closeValues)
          binding.includeCoinChart.lineChart.apply {
            moveViewToX(lineData.entryCount.toFloat())
          }
        }
      }
    }
    viewModel.error.observe(viewLifecycleOwner) {
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
    }
  }

  private fun animateChanges(old: CoinDetailsModel, new: LivePriceListResponse) {
    binding.includeCoinChart.txtCoinPriceUSD.apply {
      if (new.priceUSD > old.priceUSD)
        animateColor(
          resources.getColor(R.color.dark_blue_900),
          Color.GREEN,
          1000L
        )
      else if (new.priceUSD < old.priceUSD)
        animateColor(
          resources.getColor(R.color.dark_blue_900),
          Color.RED,
          1000L
        )
      else animateColor(
        resources.getColor(R.color.dark_blue_900),
        Color.GRAY,
        1000L
      )
    }
    binding.includeCoinChart.txtCoinPriceTMN.apply {
      if (new.priceTMN > old.priceTMN)
        animateColor(
          resources.getColor(R.color.dark_blue_900),
          Color.GREEN,
          1000L
        )
      else if (new.priceTMN < old.priceTMN)
        animateColor(
          resources.getColor(R.color.dark_blue_900),
          Color.RED,
          1000L
        )
      else animateColor(
        resources.getColor(R.color.dark_blue_900),
        Color.GRAY,
        1000L
      )
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    EventBus.getDefault().unregister(this)
    activityViewModel.unsubscribeFromCandleUpdate()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  private fun setCandleData(e: CandleEntry) {
    with(binding.includeCoinChart) {
      priceDetails.visibility = View.GONE
      candleDetails.visibility = View.VISIBLE
      txtCandleDetailsLow.text = e.low.toString()
      txtCandleDetailsHigh.text = e.high.toString()
      txtCandleDetailsOpen.text = e.open.toString()
      txtCandleDetailsClose.text = e.close.toString()
    }
    candleData = PersianDate((e.data as Number).toLong())
    binding.includeCoinChart.txtCandleDetailsDate.text = candleData?.toString()
  }

  private fun setLineData(entry: Entry) {
    with(binding.includeCoinChart) {
      if (cardCandleDetails.visibility != View.VISIBLE)
        cardCandleDetails.visibility = View.VISIBLE
      txtCandleDetails.text = StringBuilder()
        .append(toPersianNumbers("${getString(R.string.close)}: ${separatePrice(entry.y)}"))
        .toString()
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("CoinDetailsFragmentJob")

  fun changeImageWithAnimation(c: Context?, v: ImageView, new_image: Int) {
    val anim_out: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_out)
    val anim_in: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_in)
    anim_out.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationStart(animation: Animation?) {}
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationEnd(animation: Animation?) {
        v.setImageResource(new_image)
        anim_in.setAnimationListener(object: Animation.AnimationListener {
          override fun onAnimationStart(animation: Animation?) {}
          override fun onAnimationRepeat(animation: Animation?) {}
          override fun onAnimationEnd(animation: Animation?) {}
        })
        v.startAnimation(anim_in)
      }
    })
    v.startAnimation(anim_out)
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.txtSeeMoreNews -> {
        findNavController().navigate(
          CoinDetailsFragmentDirections.actionCoinDetailsFragmentToRelatedNewsFragment(
            args.coinId,
            args.coinName
          )
        )
      }
      R.id.txtSeeMoreCoins -> {
        findNavController().navigate(
          CoinDetailsFragmentDirections.actionCoinDetailsFragmentToRelatedCoinsFragment(
            args.coinId
          )
        )
      }
      R.id.txtIrt -> {
        launch {
          if (!StaticFields.isIrtSelected) {
            StaticFields.isIrtSelected = true
            withContext(Dispatchers.Main) {
              binding.includeCoinChart.txtIrt.setTextColor(resources.getColor(R.color.black))
              binding.includeCoinChart.txtUsd.setTextColor(resources.getColor(R.color.gray_400))
            }

            withContext(Dispatchers.Main) {
              binding.includeCoinChart.timeFramesList.visibility = View.INVISIBLE
              chartLoading = true
            }
            viewModel.getTimeFrames(args.coinId, TETHER_ID)
          }
        }
      }
      R.id.imgToggleChart -> {
        binding.includeCoinChart.cardCandleDetails.visibility = View.GONE
        var selectedTimeFrame: TimeFrameModel? = null
        for (i in timeFrameAdapter.differ.currentList) {
          if (i.isSelected) {
            selectedTimeFrame = i
            break
          }
        }
        when (chartType) {
          CANDLESTICK -> {
            changeImageWithAnimation(
              requireContext(),
              binding.imgToggleChart,
              R.drawable.ic_area_chart
            )
            chartType = LINEAR
            chartLoading = true
            launch {
              viewModel.getChartData(coin?.stats!!.id, selectedTimeFrame!!.id)
            }
          }
          LINEAR -> {
            changeImageWithAnimation(
              requireContext(),
              binding.imgToggleChart,
              R.drawable.ic_candlestick_chart
            )
            chartType = CANDLESTICK
            chartLoading = true
            launch {
              viewModel.getChartData(coin?.stats!!.id, selectedTimeFrame!!.id)
            }
          }
        }

      }
      R.id.txtUsd -> {
        launch {
          if (StaticFields.isIrtSelected) {
            StaticFields.isIrtSelected = false
            withContext(Dispatchers.Main) {
              binding.includeCoinChart.txtIrt.setTextColor(resources.getColor(R.color.gray_400))
              binding.includeCoinChart.txtUsd.setTextColor(resources.getColor(R.color.black))
            }

            withContext(Dispatchers.Main) {
              binding.includeCoinChart.timeFramesList.visibility = View.INVISIBLE
              chartLoading = true
            }
            viewModel.getTimeFrames(args.coinId)
          }
        }
      }
    }
  }

  @SuppressLint("UseCompatTextViewDrawableApis", "UseCompatLoadingForDrawables")
  private fun setData(model: CoinDetailsModel) {
    binding.includeCoinChart.txtCoinPriceTMN.text =
      formatPrice(
        toPersianNumbers(separatePrice(model.priceTMN)),
        currency = getString(R.string.toomans)
      )
    binding.includeCoinChart.txtCoinPriceUSD.text = formatPrice(
      toPersianNumbers(separatePrice(model.priceUSD)),
      currency = getString(R.string.dollars)
    )
    binding.includeCoinChart.txtCoinPercentage.text =
      StringBuilder(separatePrice(model.percentage.toFloat()))
        .append(" ")
        .append("%")
        .toString()
    binding.includeCoinChart.percentageRoot.setBackgroundResource(
      if (model.percentage > 0) R.drawable.ascending_background
      else if (model.percentage < 0) R.drawable.descending_background
      else R.drawable.neutral_background
    )

    binding.imgFavorite.apply {
      if (model.isWatchlist) setImageResource(R.drawable.ic_baseline_visibility_off_24) else setImageResource(
        R.drawable.ic_baseline_visibility_24
      )
    }


//    Glide.with(this)
//      .load(if (model.percentage > 0) R.drawable.ic_arrow_ascend else R.drawable.ic_arrow_descend)
//      .into(binding.includeCoinChart.imgAscend)

    binding.includeCoinChart.txtCoinPercentage.apply {
      compoundDrawablePadding = 8
      compoundDrawableTintList = ColorStateList.valueOf(Color.WHITE)
      var (startDrawable, topDrawable, endDrawable, bottomDrawable) = compoundDrawablesRelative
      startDrawable =
        if (model.percentage > 0) resources.getDrawable(R.drawable.ic_round_arrow_drop_up_24) else resources.getDrawable(
          R.drawable.ic_round_arrow_drop_down_24
        )
      setCompoundDrawablesRelative(startDrawable, topDrawable, endDrawable, bottomDrawable)
    }

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

  private fun initNewsList() {
    binding.includeCoinMisc.relatedNewsList.apply {
      this.layoutManager = newsManager
      adapter = newsAdapter
    }
  }

  private fun initCoinsList() {
    binding.includeCoinMisc.relatedCoinsList.apply {
      layoutManager = coinManager
      adapter = coinAdapter
    }
  }

  private fun initTimeFramesList() {
    binding.includeCoinChart.timeFramesList.apply {
      layoutManager = timeFrameManager
      adapter = timeFrameAdapter
    }
  }
}