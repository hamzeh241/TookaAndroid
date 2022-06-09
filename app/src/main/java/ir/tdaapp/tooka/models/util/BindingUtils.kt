package ir.tdaapp.tooka.models.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.EmptyStateBinding
import ir.tdaapp.tooka.databinding.EmptyStateHorizontalBinding
import ir.tdaapp.tooka.models.components.TookaCandlestickChart
import ir.tdaapp.tooka.models.components.TookaLineChart
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.util.CompoundPosition.*
import java.math.BigDecimal
import java.math.RoundingMode

infix fun TextView.text(value: CharSequence) = this.setText(value)

infix fun TextView.text(@StringRes resId: Int) = this.setText(resId)

/**
 * Serfan be manzure rahat tar budan kar
 */
infix fun ImageView.glideUrl(url: String) {
  Glide.with(this.context)
    .load(url)
    .placeholder(R.drawable.ic_baseline_circle_24)
    .into(this)
}

fun Fragment.emptyStateViewVertical(
  @StringRes title: Int,
  @StringRes content: Int? = null,
  @RawRes lottieRawRes: Int? = null,
  titleConfig: (TextView)->Unit = {},
  contentConfig: (TextView)->Unit = {},
  animationConfig: (LottieAnimationView)->Unit = {},
): View {
  val binding = EmptyStateBinding.inflate(layoutInflater)
  binding.emptyStateTitle.text = getString(title)

  binding.emptyStateTitle.apply(titleConfig)
  binding.emptyStateContent.apply(contentConfig)
  binding.emptyStateAnimation.apply(animationConfig)

  if (content != null) {
    binding.emptyStateContent.text = getString(content)
  } else {
    binding.emptyStateContent.visibility = View.GONE
  }

  if (lottieRawRes != null) {
    binding.emptyStateAnimation.setAnimation(lottieRawRes)
    binding.emptyStateAnimation.playAnimation()
  } else {
    binding.emptyStateAnimation.visibility = View.GONE
  }

  return binding.root
}

fun Fragment.emptyStateViewHorizontal(
  @StringRes title: Int = R.string.nothing_here,
  @StringRes content: Int? = null,
  @RawRes lottieRawRes: Int? = null,
  titleConfig: (TextView)->Unit = {},
  contentConfig: (TextView)->Unit = {},
  animationConfig: (LottieAnimationView)->Unit = {},
): View {
  val binding = EmptyStateHorizontalBinding.inflate(layoutInflater)
  binding.emptyStateTitle.text = getString(title)

  binding.emptyStateTitle.apply(titleConfig)
  binding.emptyStateContent.apply(contentConfig)
  binding.emptyStateAnimation.apply(animationConfig)

  if (content != null) {
    binding.emptyStateContent.text = getString(content)
  } else {
    binding.emptyStateContent.visibility = View.GONE
  }

  if (lottieRawRes != null) {
    binding.emptyStateAnimation.setAnimation(lottieRawRes)
    binding.emptyStateAnimation.playAnimation()
  } else {
    binding.emptyStateAnimation.visibility = View.GONE
  }

  return binding.root
}

fun getCorrectNumberFormat(input: String, c: Context): String = when (getCurrentLocale(c)) {
  "en" -> toEnglishNumbers(input)
  "fa" -> toPersianNumbers(input)
  else -> toEnglishNumbers(input)
}

fun getCorrectNumberFormat(input: Float, c: Context): String =
  input.toString().let {
    getCorrectNumberFormat(it, c)
  }


fun View.addSpringAnimation() {
  val scaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 0.90f)
  val scaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 0.90f)

  setOnTouchListener { v, event ->
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        scaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
        scaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        scaleXAnim.start()

        scaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
        scaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        scaleYAnim.start()
      }
      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL -> {
        scaleXAnim.cancel()
        scaleYAnim.cancel()
        val reverseScaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 1f)
        reverseScaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
        reverseScaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        reverseScaleXAnim.start()
        val reverseScaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 1f)
        reverseScaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
        reverseScaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        reverseScaleYAnim.start()
      }
    }

    false
  }
}

enum class CompoundPosition {
  START,
  END,
  TOP,
  BOTTOM
}

fun TextView.isLoading(
  value: Boolean,
  @ColorInt tint: Int = Color.WHITE,
  position: CompoundPosition = START
) {
  val progressDrawable = ProgressBar(context).indeterminateDrawable.apply {
    // apply any customization on drawable. not on progress view
    setBounds(0, 0, 24.toPx, 24.toPx)
    setTint(tint)
  }
  apply {
    compoundDrawablePadding = 8
    val (startDrawable, topDrawable, endDrawable, bottomDrawable) = compoundDrawablesRelative
    if (value) {
      when (position) {
        START -> {
          setCompoundDrawablesRelative(
            progressDrawable,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
        }
        END -> {
          setCompoundDrawablesRelative(
            startDrawable,
            topDrawable,
            progressDrawable,
            bottomDrawable
          )
        }
        TOP -> {
          setCompoundDrawablesRelative(
            startDrawable,
            progressDrawable,
            endDrawable,
            bottomDrawable
          )
        }
        BOTTOM -> {
          setCompoundDrawablesRelative(
            startDrawable,
            topDrawable,
            endDrawable,
            progressDrawable
          )
        }
      }
      // add progress and keep others

      (progressDrawable as? Animatable)?.start()
    } else {
      // remove progress
      when (position) {
        START -> {
          setCompoundDrawablesRelative(
            null,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
        }
        END -> {
          setCompoundDrawablesRelative(
            startDrawable,
            topDrawable,
            null,
            bottomDrawable
          )
        }
        TOP -> {
          setCompoundDrawablesRelative(
            startDrawable,
            null,
            endDrawable,
            bottomDrawable
          )
        }
        BOTTOM -> {
          setCompoundDrawablesRelative(
            startDrawable,
            topDrawable,
            endDrawable,
            null
          )
        }
      }
      (progressDrawable as? Animatable)?.stop()
    }
  }
}

infix fun ImageView.glideRes(@DrawableRes resId: Int) {
  Glide.with(this.context)
    .load(resId)
    .into(this)
}

fun EditText.disableKeyboard() = this.apply {
//  setRawInputType(InputType.TYPE_CLASS_TEXT)
//  setTextIsSelectable(true)
  setRawInputType(InputType.TYPE_NULL);
  setFocusable(true);
}

fun TextView.setPrice(price: Number) {
  text = StringBuilder(getCorrectNumberFormat(separatePrice(price), context)).toString()
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
  text = StringBuilder(
    BigDecimal(model.percentage.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
  )
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

inline fun TookaLineChart.addListener(
  crossinline onNothingSelected: ()->Unit = {},
  crossinline onValueSelected: (e: Entry?, h: Highlight?)->Unit = { e, h -> }
) {
  this.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
    override fun onValueSelected(e: Entry?, h: Highlight?) {
      onValueSelected.invoke(e, h)
    }

    override fun onNothingSelected() {
      onNothingSelected.invoke()
    }
  })
}

inline fun TookaCandlestickChart.addListener(
  crossinline onNothingSelected: ()->Unit = {},
  crossinline action: (e: Entry?, h: Highlight?)->Unit = { e, h -> }
): OnChartValueSelectedListener {
  val listener = object: OnChartValueSelectedListener {
    override fun onValueSelected(e: Entry?, h: Highlight?) {
      action(e, h)
    }

    override fun onNothingSelected() {
      onNothingSelected()
    }
  }
  setOnChartValueSelectedListener(listener)
  return listener
}