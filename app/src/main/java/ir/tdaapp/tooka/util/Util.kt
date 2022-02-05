package ir.tdaapp.tooka.util

import ContextUtils
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import ir.tdaapp.tooka.R
import java.lang.StringBuilder
import java.util.*

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.getColor
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.gson.reflect.TypeToken
import ir.tdaapp.tooka.components.ChartMarkerView
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.CoinDetailsModel
import ir.tdaapp.tooka.models.CompareOHLCVModel
import ir.tdaapp.tooka.models.ResponseModel
import java.text.DecimalFormat
import kotlin.reflect.KClass
import com.google.gson.Gson
import kotlin.collections.ArrayList


const val VIEW_TYPE_LINEAR = 0
const val VIEW_TYPE_GRID = 1

fun maskTextView(textView: TextView) {
  val string = StringBuilder()
  for (i in 1..textView.length()) {
    string.append("*")
  }

  textView.text = string.toString()
}

fun TextView.animateColor(
  fromColor: Int,
  toColor: Int,
  animDuration: Long = 300L,
  onEnd: (animator: ObjectAnimator)->Unit? = {}
) {
  val valueAnimator: ValueAnimator = ObjectAnimator.ofInt(
    this,
    "textColor",
    fromColor,
    toColor
  )
  valueAnimator.apply {
    setEvaluator(ArgbEvaluator())
    duration = animDuration
    repeatCount = 1
    repeatMode = ValueAnimator.REVERSE
    start()
    doOnEnd {
      onEnd.invoke(this as ObjectAnimator)
    }
  }
}


fun String.separatePrice(price: Float): String {
  val decim = DecimalFormat("#,###.##")
  return decim.format(price)
}

fun random(start: Int, end: Int): Int {
  require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
  return Random(System.nanoTime()).nextInt(end - start + 1) + start
}

inline fun <reified T: Any> convertResponse(json: String): ResponseModel<T> {
  val collectionType = object: TypeToken<ResponseModel<T>>() {}.type
  val response: ResponseModel<T> =
    GsonInstance.getInstance().fromJson(json, collectionType)

  return response
}

infix fun <T> ArrayList<T>.insert(value: T) = this.add(value)

fun detectError(response: ResponseModel<Any>): NetworkErrors {
  when (response.code) {
    400 -> return NetworkErrors.CLIENT_ERROR
    404 -> return NetworkErrors.NOT_FOUND_ERROR
    500 -> return NetworkErrors.SERVER_ERROR
    401 -> return NetworkErrors.UNAUTHORIZED_ERROR
    else -> return NetworkErrors.UNKNOWN_ERROR
  }
}

fun separatePrice(price: Float): String {
  val decim = DecimalFormat("#,###.##")
  return decim.format(price)
}

fun separatePrice(price: Double): String {
  val decim = DecimalFormat("#,###.##")
  return decim.format(price.toFloat())
}

fun separatePrice(price: String): String {
  val decim = DecimalFormat("#,###.##")
  return decim.format(price)
}

fun String.toPersianNumbers(value: String): String {
  var newValue = value
  newValue = newValue.replace("0", "۰")
  newValue = newValue.replace("1", "۱")
  newValue = newValue.replace("2", "۲")
  newValue = newValue.replace("3", "۳")
  newValue = newValue.replace("4", "۴")
  newValue = newValue.replace("5", "۵")
  newValue = newValue.replace("6", "۶")
  newValue = newValue.replace("7", "۷")
  newValue = newValue.replace("8", "۸")
  newValue = newValue.replace("9", "۹")
  newValue = newValue.replace(",", "٬")

  return newValue
}

fun toPersianNumbers(value: String): String {
  var newValue = value
  newValue = newValue.replace("0", "۰")
  newValue = newValue.replace("1", "۱")
  newValue = newValue.replace("2", "۲")
  newValue = newValue.replace("3", "۳")
  newValue = newValue.replace("4", "۴")
  newValue = newValue.replace("5", "۵")
  newValue = newValue.replace("6", "۶")
  newValue = newValue.replace("7", "۷")
  newValue = newValue.replace("8", "۸")
  newValue = newValue.replace("9", "۹")
  newValue = newValue.replace(",", "٬")

  return newValue
}

fun toEnglishNumbers(value: String): String {
  var newValue = value
  newValue = newValue.replace("۰", "0")
  newValue = newValue.replace("۱", "1")
  newValue = newValue.replace("۲", "2")
  newValue = newValue.replace("۳", "3")
  newValue = newValue.replace("۴", "4")
  newValue = newValue.replace("۵", "5")
  newValue = newValue.replace("۶", "6")
  newValue = newValue.replace("۷", "7")
  newValue = newValue.replace("۸", "8")
  newValue = newValue.replace("۹", "9")
  newValue = newValue.replace("٬", ",")

  return newValue
}

fun String.toEnglishNumbers(value: String): String {
  var newValue = value
  newValue = newValue.replace("۰", "0")
  newValue = newValue.replace("۱", "1")
  newValue = newValue.replace("۲", "2")
  newValue = newValue.replace("۳", "3")
  newValue = newValue.replace("۴", "4")
  newValue = newValue.replace("۵", "5")
  newValue = newValue.replace("۶", "6")
  newValue = newValue.replace("۷", "7")
  newValue = newValue.replace("۸", "8")
  newValue = newValue.replace("۹", "9")
  newValue = newValue.replace("٬", ",")

  return newValue
}

fun openWebpage(context: Context, url: String) {
  val i = Intent(Intent.ACTION_VIEW)
  i.data = Uri.parse(url)
  context.startActivity(i)
}

fun share(context: Context, message: String) {
  val share = Intent(Intent.ACTION_SEND)
  share.type = "text/plain"
  share.putExtra(Intent.EXTRA_TEXT, message)

  context.startActivity(Intent.createChooser(share, context.getString(R.string.send_to)))
}

fun isMainThread(): Boolean = if (Looper.getMainLooper() == Looper.myLooper()) true else false

fun getCurrentLocale(context: Context): String? {
  return ContextUtils.getLocale(context).toString()
}

fun getName(context: Context,coin: Coin): String {
  return when (getCurrentLocale(context)) {
    "fa" -> {
      if (coin.persianName != null)
        coin.persianName!!
      else coin.name
    }
    else -> coin.name
  }
}

fun setCandleChart(
  candleStickChart: CandleStickChart,
  coin: CoinDetailsModel,
  models: List<List<Any>>
) {
  val candleStickChart = candleStickChart
  candleStickChart.isHighlightPerDragEnabled = true

  candleStickChart.setDrawBorders(false)
  candleStickChart.setDrawGridBackground(false)
  candleStickChart.description = Description().apply {
    this.text = StringBuilder(coin?.name!!)
      .append(" (")
      .append(coin?.symbol)
      .append(")").toString()
  }

  val yAxis = candleStickChart.axisLeft
  val rightAxis = candleStickChart.axisRight
  rightAxis.isEnabled = false
  yAxis.setDrawGridLines(false)
  rightAxis.setDrawGridLines(false)
  candleStickChart.requestDisallowInterceptTouchEvent(false)

  val xAxis = candleStickChart.xAxis

  xAxis.setDrawGridLines(false) // disable x axis grid lines

  xAxis.setDrawLabels(false)
  rightAxis.textColor = Color.WHITE
  yAxis.setDrawLabels(true)
  yAxis.setDrawGridLines(true)
  xAxis.granularity = 1f
  xAxis.isGranularityEnabled = true
  xAxis.setAvoidFirstLastClipping(true)

  val l = candleStickChart.legend
  l.isEnabled = false

  val yValsCandleStick = ArrayList<CandleEntry>()
  var specifiedCandle: CandleEntry?
  specifiedCandle = null
  for (a in models) {
    specifiedCandle = CandleEntry(
      models.indexOf(a).toFloat(),
      a[2].toString().toFloat(),
      a[3].toString().toFloat(),
      a[1].toString().toFloat(),
      a[4].toString().toFloat()
    )
    specifiedCandle.data = a[0]
    yValsCandleStick.add(
      specifiedCandle
    )
  }

  val set1 = CandleDataSet(yValsCandleStick, coin?.symbol)
  set1.color = Color.rgb(80, 80, 80)
  set1.shadowColor = Color.parseColor("#eeeeee")
  set1.shadowWidth = 0.8f
  set1.decreasingColor = candleStickChart.context.resources.getColor(R.color.red_200)
  set1.decreasingPaintStyle = Paint.Style.FILL
  set1.increasingColor = candleStickChart.context.resources.getColor(R.color.green_300)
  set1.increasingPaintStyle = Paint.Style.FILL
  set1.neutralColor = Color.LTGRAY
  set1.highLightColor = Color.GRAY
  set1.setDrawValues(false)
  set1.setDrawHorizontalHighlightIndicator(false)

  candleStickChart.clear()
  val data = CandleData(set1)

  candleStickChart.data = data
  candleStickChart.invalidate()
}

fun setChartData(lineChart: LineChart, priceList: List<Double>) {
  val values = ArrayList<Entry>()
  var a = 0
  for (i in priceList) {
    val value = i
    values.add(Entry(a.toFloat(), value.toFloat()))
    a++
  }
  val set1: LineDataSet
  if (lineChart.getData() != null &&
    lineChart.getData().getDataSetCount() > 0
  ) {
    set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
    set1.values = values
    lineChart.getData().notifyDataChanged()
    lineChart.notifyDataSetChanged()
  } else {
    // create a dataset and give it a type
    set1 = LineDataSet(values, "")
    set1.apply {
      mode = LineDataSet.Mode.HORIZONTAL_BEZIER
      cubicIntensity = 0.2f
      setDrawFilled(true)
      setDrawCircles(false)
      lineWidth = 1.8f
      circleRadius = 2f
      circleHoleColor = Color.BLUE
      setCircleColor(Color.BLUE)
      highLightColor = Color.GRAY
      highlightLineWidth = 1f
      color = getColor(lineChart.context, R.color.gold_300)
      fillColor = getColor(lineChart.context, R.color.gold_300)
      fillAlpha = 50
      setDrawHorizontalHighlightIndicator(false)
      fillFormatter =
        IFillFormatter { dataSet: ILineDataSet?, dataProvider: LineDataProvider? ->
          lineChart.getAxisLeft().getAxisMinimum()
        }

      val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.green_gradient)
      fillDrawable = drawable
    }

    // create a data object with the data sets
    val data = LineData(set1)
    data.apply {
      setValueTextSize(8f)
      setDrawValues(false)
    }

    lineChart.apply {
      getAxisLeft().setEnabled(true)
      getAxisRight().setEnabled(true)
      getAxisLeft().setDrawGridLines(false)
      getAxisLeft().setDrawAxisLine(false)
//      getAxisLeft().removeAllLimitLines()
//      getAxisRight().removeAllLimitLines()
      getAxisRight().setDrawAxisLine(false)
      getAxisRight().setDrawAxisLine(false)
      getXAxis().setEnabled(false)
      val xAxis = lineChart.xAxis

      xAxis.setDrawGridLines(false) // disable x axis grid lines
      xAxis.setDrawLabels(false)

      xAxis.granularity = 1f
      xAxis.isGranularityEnabled = true
      xAxis.setAvoidFirstLastClipping(true)
      xAxis.setDrawAxisLine(false)
      xAxis.setDrawGridLines(true)
      axisRight.setDrawLabels(false)
      axisRight.isEnabled = false
      val manager = this.context.assets
      axisRight.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")
      axisLeft.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")

      axisLeft.textSize = 8f
      axisLeft.setDrawGridLines(true)
      axisRight.textSize = 8f
      getLegend().setEnabled(false)
      getDescription().setEnabled(false)
      setTouchEnabled(true)
      animateXY(1000, 1000)
//      val markerView = ChartMarkerView(lineChart.context, R.layout.component_marker_view)
//      this.markerView = markerView

      val yAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT)
      yAxis.isEnabled = false
      clear()
      setData(data)
      invalidate()
    }
  }
}

fun setCoinDetailsChartData(lineChart: LineChart, priceList: List<Double>) {
  val values = ArrayList<Entry>()
  var a = 0
  for (i in priceList) {
    val value = i
    values.add(Entry(a.toFloat(), value.toFloat()))
    a++
  }
  val set1: LineDataSet
  if (lineChart.getData() != null &&
    lineChart.getData().getDataSetCount() > 0
  ) {
    set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
    set1.values = values
    lineChart.getData().notifyDataChanged()
    lineChart.notifyDataSetChanged()
  } else {
    // create a dataset and give it a type
    set1 = LineDataSet(values, "")
    set1.apply {
      mode = LineDataSet.Mode.HORIZONTAL_BEZIER
      cubicIntensity = 0.2f
      setDrawFilled(true)
      setDrawCircles(false)
      lineWidth = 1.8f
      circleRadius = 2f
      circleHoleColor = Color.BLUE
      setCircleColor(Color.BLUE)
      highLightColor = Color.GRAY
      highlightLineWidth = 1f
      color = getColor(lineChart.context, R.color.gold_300)
      fillColor = getColor(lineChart.context, R.color.gold_300)
      fillAlpha = 50
      setDrawHorizontalHighlightIndicator(false)
      fillFormatter =
        IFillFormatter { dataSet: ILineDataSet?, dataProvider: LineDataProvider? ->
          lineChart.getAxisLeft().getAxisMinimum()
        }

//      val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.green_gradient)
//      fillDrawable = drawable
    }

    // create a data object with the data sets
    val data = LineData(set1)
    data.apply {
      setValueTextSize(8f)
      setDrawValues(false)
    }

    lineChart.apply {
      getAxisLeft().setEnabled(true)
      getAxisRight().setEnabled(true)
      getAxisLeft().setDrawGridLines(false)
      getAxisLeft().setDrawAxisLine(false)
//      getAxisLeft().removeAllLimitLines()
//      getAxisRight().removeAllLimitLines()
      getAxisRight().setDrawAxisLine(false)
      getAxisRight().setDrawAxisLine(false)
      getXAxis().setEnabled(false)
      val xAxis = lineChart.xAxis

      xAxis.setDrawGridLines(false) // disable x axis grid lines
      xAxis.setDrawLabels(false)

      xAxis.granularity = 1f
      xAxis.isGranularityEnabled = true
      xAxis.setAvoidFirstLastClipping(true)
      xAxis.setDrawAxisLine(false)
      xAxis.setDrawGridLines(true)
      axisRight.setDrawLabels(false)
      axisRight.isEnabled = false
      val manager = this.context.assets
      axisRight.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")
      axisLeft.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")

      axisLeft.textSize = 8f
      axisLeft.setDrawGridLines(true)
      axisRight.textSize = 8f
      getLegend().setEnabled(false)
      getDescription().setEnabled(false)
      setTouchEnabled(true)
      animateXY(1000, 1000)
//      val markerView = ChartMarkerView(lineChart.context, R.layout.component_marker_view)
//      this.markerView = markerView

      val yAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT)
      yAxis.isEnabled = false
      clear()
      setData(data)
      invalidate()
    }
  }
}

fun setCompareChartData(lineChart: LineChart, priceList: CompareOHLCVModel) {
  val first = ArrayList<Entry>()
  val second = ArrayList<Entry>()
  var a = 0
  for (i in priceList.first.reversed()) {
    val value = i
    first.add(Entry(a.toFloat(), (value[0] as Double).toFloat()))
    a++
  }
  var b = 0
  for (i in priceList.second.reversed()) {
    val value = i
    second.add(Entry(b.toFloat(), (value[0] as Double).toFloat()))
    b++
  }
  val set1: LineDataSet
  val set2: LineDataSet
  if (lineChart.getData() != null &&
    lineChart.getData().getDataSetCount() > 0
  ) {
    set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
    set2 = lineChart.getData().getDataSetByIndex(1) as LineDataSet
    set1.values = first
    set2.values = second
    lineChart.getData().notifyDataChanged()
    lineChart.notifyDataSetChanged()
  } else {
    // create a dataset and give it a type
    set1 = LineDataSet(first, "")
    set2 = LineDataSet(second, "")
    set1.apply {
      mode = LineDataSet.Mode.HORIZONTAL_BEZIER
      cubicIntensity = 0.2f
      setDrawFilled(true)
      setDrawCircles(false)
      lineWidth = 1.8f
      circleRadius = 2f
      circleHoleColor = Color.BLUE
      setCircleColor(Color.BLUE)
      highLightColor = Color.GRAY
      highlightLineWidth = 1f
      color = getColor(lineChart.context, R.color.gold_300)
//      fillColor = getColor(lineChart.context, R.color.gold_300)
      fillAlpha = 50
      setDrawHorizontalHighlightIndicator(false)
      fillFormatter =
        IFillFormatter { dataSet: ILineDataSet?, dataProvider: LineDataProvider? ->
          lineChart.getAxisLeft().getAxisMinimum()
        }

//      val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.green_gradient)
//      fillDrawable = drawable
    }
    set2.apply {
      mode = LineDataSet.Mode.HORIZONTAL_BEZIER
      cubicIntensity = 0.2f
      setDrawFilled(true)
      setDrawCircles(false)
      lineWidth = 1.8f
      circleRadius = 2f
      circleHoleColor = Color.BLUE
      setCircleColor(Color.BLUE)
      highLightColor = Color.GRAY
      highlightLineWidth = 1f
      color = getColor(lineChart.context, R.color.dark_blue_800)
//      fillColor = getColor(lineChart.context, R.color.gold_300)
      fillAlpha = 50
      setDrawHorizontalHighlightIndicator(false)
      fillFormatter =
        IFillFormatter { dataSet: ILineDataSet?, dataProvider: LineDataProvider? ->
          lineChart.getAxisLeft().getAxisMinimum()
        }

//      val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.green_gradient)
//      fillDrawable = drawable
    }

    // create a data object with the data sets
    val data1 = LineData(set1, set2)
    data1.apply {
      setValueTextSize(8f)
      setDrawValues(false)
    }

    lineChart.apply {
      getAxisLeft().setEnabled(true)
      getAxisRight().setEnabled(true)
      getAxisLeft().setDrawGridLines(false)
      getAxisLeft().setDrawAxisLine(false)
//      getAxisLeft().removeAllLimitLines()
//      getAxisRight().removeAllLimitLines()
      getAxisRight().setDrawAxisLine(false)
      getAxisRight().setDrawAxisLine(false)
      getXAxis().setEnabled(false)
      val xAxis = lineChart.xAxis

      xAxis.setDrawGridLines(false) // disable x axis grid lines
      xAxis.setDrawLabels(false)

      xAxis.granularity = 1f
      xAxis.isGranularityEnabled = true
      xAxis.setAvoidFirstLastClipping(true)
      xAxis.setDrawAxisLine(false)
      xAxis.setDrawGridLines(true)
      axisRight.setDrawLabels(false)
      axisRight.isEnabled = false
      val manager = this.context.assets
      axisRight.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")
      axisLeft.typeface =
        Typeface.createFromAsset(manager, "iran_yekan_reqular_mobile_fa_num.ttf")

      axisLeft.textSize = 8f
      axisLeft.setDrawGridLines(true)
      axisRight.textSize = 8f
      getLegend().setEnabled(false)
      getDescription().setEnabled(false)
      setTouchEnabled(true)
      animateXY(1000, 1000)
//      val markerView = ChartMarkerView(lineChart.context, R.layout.component_marker_view)
//      this.markerView = markerView

      val yAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT)
      yAxis.isEnabled = false
      clear()
      setData(data1)
      invalidate()
    }
  }
}

fun toast(context: Context, message: String) {
  Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun setMiniChart(lineChart: LineChart, priceList: List<Double>) {
  val values = ArrayList<Entry>()
  for (i in priceList.indices) {
    val value = priceList[i]
    values.add(Entry(i.toFloat(), value.toFloat()))
  }
  val set1: LineDataSet
  if (lineChart.getData() != null &&
    lineChart.getData().getDataSetCount() > 0
  ) {
    set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
    set1.values = values
    lineChart.getData().notifyDataChanged()
    lineChart.notifyDataSetChanged()
  } else {
    // create a dataset and give it a type
    set1 = LineDataSet(values, "")
    set1.apply {
      mode = LineDataSet.Mode.HORIZONTAL_BEZIER
      cubicIntensity = 0.2f
      setDrawFilled(true)
      setDrawCircles(false)
      lineWidth = 1.8f
      circleRadius = 2f
      circleHoleColor = Color.BLUE
      setCircleColor(Color.BLUE)
      highLightColor = /*Color.rgb(244, 117, 117)*/getColor(lineChart.context, R.color.white_900)
      highlightLineWidth = 2f
      color = /*getColor(lineChart.context, R.color.gold_300)*/
        getColor(lineChart.context, R.color.white_900)
      fillColor = /*getColor(lineChart.context, R.color.gold_300)*/
        getColor(lineChart.context, R.color.white_900)
      fillAlpha = 50
      setDrawHorizontalHighlightIndicator(true)
      fillFormatter =
        IFillFormatter { dataSet: ILineDataSet?, dataProvider: LineDataProvider? ->
          lineChart.getAxisLeft().getAxisMinimum()
        }

      val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.green_gradient)
      fillDrawable = drawable
    }

    // create a data object with the data sets
    val data = LineData(set1)
    data.apply {
      setValueTextSize(8f)
      setDrawValues(false)
    }

    lineChart.apply {
      getAxisLeft().setEnabled(false)
      getAxisRight().setEnabled(false)
      getAxisLeft().setDrawGridLines(false)
      getAxisLeft().setDrawAxisLine(false)
//      getAxisLeft().removeAllLimitLines()
//      getAxisRight().removeAllLimitLines()
      getAxisRight().setDrawAxisLine(false)
      getAxisRight().setDrawAxisLine(false)
      getXAxis().setEnabled(false)
      xAxis.setDrawAxisLine(false)
      xAxis.setDrawGridLines(false)
      axisRight.setDrawLabels(false)
      axisRight.isEnabled = false
      val manager = this.context.assets
      axisLeft.setDrawGridLines(false)
      getLegend().setEnabled(false)
      getDescription().setEnabled(false)
      setTouchEnabled(false)
//      animateXY(1500, 1000)
      val yAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT)
      yAxis.isEnabled = false

      setData(data)
    }
  }
}

fun setCorrectMargins(cardView: CardView, context: Context, position: Int, lastPosition: Int) {
  val params = cardView.layoutParams as ViewGroup.MarginLayoutParams
  params.setMargins(
    context.resources.getDimension(R.dimen.margin4).toInt(),
    context.resources.getDimension(R.dimen.margin8).toInt(),
    context.resources.getDimension(R.dimen.margin4).toInt(),
    context.resources.getDimension(R.dimen.margin8).toInt()
  )
  when (ContextUtils.getLocale(context).toString()) {
    "fa" -> {
      if (position == 0) {
        params.setMargins(
          context.resources.getDimension(R.dimen.margin4).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt()
        )
      } else if (position == lastPosition) {
        params.setMargins(
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin4).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt()
        )
      }
    }
    "en" -> {
      if (position == 0) {
        params.setMargins(
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin4).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt()
        )
      } else if (position == lastPosition) {
        params.setMargins(
          context.resources.getDimension(R.dimen.margin4).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt(),
          context.resources.getDimension(R.dimen.margin8).toInt()
        )
      }
    }
  }
}