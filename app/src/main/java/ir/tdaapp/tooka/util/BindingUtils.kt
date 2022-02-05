package ir.tdaapp.tooka.util

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.Coin
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.RoundingMode

fun ImageView.imageUrl(url: String) {
  val image = "https://razhashop.ir/Images/Coins/" + url
  Glide.with(this.context)
    .load(image)
    .into(this)
}

fun TextView.setPrice(price: Double) {
  text = StringBuilder(separatePrice(price.toFloat())).toString()
}

fun TextView.setCoinName(model: Coin) {
  text = when (ContextUtils.getLocale(this.context).toString()) {
    "fa" -> if (model.persianName != null) model.persianName else model.name
    else -> model.name
  }
}

fun LineChart.setData(ohlc: List<Double>) = setMiniChart(this, ohlc.reversed())


fun ImageView.identifyAscending(coin: Coin) = this.apply {
  setImageResource(
    when {
      coin.percentage > 0 -> {
        R.drawable.ic_ascend
      }
      coin.percentage < 0 -> {
        R.drawable.ic_descend
      }
      coin.percentage == 0.0f -> {
        R.drawable.ic_remove
      }
      else -> {
        TODO("Will never be reached!")
      }
    }
  )
}

fun TextView.setPercentage(model: Coin) = this.apply {
  text = StringBuilder(BigDecimal(model.percentage.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toPlainString())
    .append(" ")
    .append("%")
    .toString()

  setTextColor(
    when {
      model.percentage > 0 -> {
        R.color.green_400
      }
      model.percentage < 0 -> {
        R.color.red_600
      }
      model.percentage == 0.0f -> {
        R.color.white_900
      }
      else -> {
        TODO("Will never be reached!")
      }
    }
  )
}