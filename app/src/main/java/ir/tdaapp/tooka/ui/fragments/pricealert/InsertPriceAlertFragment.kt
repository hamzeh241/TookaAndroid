package ir.tdaapp.tooka.ui.fragments.pricealert

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentInsertPriceAlertBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.*
import ir.tdaapp.tooka.models.util.NetworkErrors.*
import ir.tdaapp.tooka.models.util.NetworkErrors.NETWORK_ERROR
import ir.tdaapp.tooka.models.util.NetworkErrors.SERVER_ERROR
import ir.tdaapp.tooka.models.util.NetworkErrors.UNKNOWN_ERROR
import ir.tdaapp.tooka.viewmodels.PriceAlertViewModel
import ir.tdaapp.tooka.viewmodels.PriceAlertViewModel.AlertStatus.*
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.math.truncate

class InsertPriceAlertFragment: BaseFragmentSecond(), View.OnClickListener, CoroutineScope {

  private lateinit var binding: FragmentInsertPriceAlertBinding
  private lateinit var handler: Handler
  private lateinit var runnable: Runnable

  private val viewModel by inject<PriceAlertViewModel>()
  private var coinModel: LivePriceListResponse? = null

  private var mPrice: Double = 0.0
  private var coinId = 0
  private var isIrt = false
    set(value) {
      if (value) {
        binding.txtIrt.setTextColor(resources.getColor(R.color.black))
        binding.txtUsd.setTextColor(resources.getColor(R.color.gray_400))
      } else {
        binding.txtIrt.setTextColor(resources.getColor(R.color.gray_400))
        binding.txtUsd.setTextColor(resources.getColor(R.color.black))
      }
      field = value
    }

  companion object {
    private const val DELAY = 50L
  }

  private lateinit var progressDrawable: Drawable

  private var isLoading: Boolean = false
    set(value) {
      if (isLoading == value) return
      field = value

      binding.textView2.apply {
        compoundDrawablePadding = 8
        val (_, topDrawable, endDrawable, bottomDrawable) = compoundDrawablesRelative
        if (value) {
          // add progress and keep others
          setCompoundDrawablesRelative(
            progressDrawable,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.start()
        } else {
          // remove progress
          setCompoundDrawablesRelative(
            null,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.stop()
        }
      }
    }

  override fun init() {
    handler = Handler()
    coinId = InsertPriceAlertFragmentArgs.fromBundle(requireArguments()).coinId

    progressDrawable = ProgressBar(context).indeterminateDrawable.apply {
      // apply any customization on drawable. not on progress view
      setBounds(0, 0, 24.toPx, 24.toPx)
      setTint(Color.WHITE)
    }

    lifecycleScope.launchWhenCreated {
      viewModel.getCoinPrice(coinId)
    }

    val maxTextScale = binding.edtPrice.textSize
    val minTextScale = 0.2 * maxTextScale

    binding.edtPrice.addTextChangedListener(FormatPrice(binding.edtPrice))
    binding.edtPrice.doOnTextChanged { text, _, _, _ ->
      try {
        mPrice = if (text != "") {
          val pricestr = text.toString()
            .replace("٬", "")
            .replace("٫", ".")
            .replace(",", "")
            .toEnglishNumbers()
          pricestr.toDouble()
        } else 0.0
      } catch (e: Exception) {
        Log.i("TOOKATAG", "init: reached")
        mPrice = 0.0
        binding.edtPrice.setText(mPrice.toString())
      }

      val paint = TextPaint(binding.edtPrice.paint)
      val desiredTextWidth = StaticLayout.getDesiredWidth(text, paint)

      val ensureWiggleRoom = 0.95F
      val scaleFactor = binding.edtPrice.width / desiredTextWidth
      val candidateTextSize = truncate(binding.edtPrice.textSize * scaleFactor * ensureWiggleRoom)
      if (candidateTextSize > minTextScale && candidateTextSize < maxTextScale) {
        binding.edtPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, candidateTextSize)
      }
    }

  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
  }

  override fun initListeners(view: View) {
    binding.txtIrt.setOnClickListener(this)
    binding.txtUsd.setOnClickListener(this)
    binding.cardView.setOnClickListener(this)
  }

  override fun initObservables() {
    viewModel.price.observe(viewLifecycleOwner) {
      coinModel = it
      mPrice = it.priceUSD
      binding.edtPrice.setText(it.priceUSD.toBigDecimal().toPlainString())
    }
    viewModel.alertResult.observe(viewLifecycleOwner) {
      isLoading = false

      val snack = Snackbar.make(
        binding.root,
        resources.getString(R.string.alert_submitted),
        Snackbar.LENGTH_SHORT
      )

      val textView =
        snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
      textView.typeface = Typeface.createFromAsset(
        requireActivity().assets,
        "iranyekan_medium.ttf"
      )
      snack.view.setBackgroundResource(R.drawable.ok_snackbar_background)

      snack.show()

      requireActivity().onBackPressed()
    }
  }

  override fun initErrors() {
    viewModel.error.observe(viewLifecycleOwner) {
      isLoading = false

      var icon = 0
      var text = 0
      var color = 0
      when (it) {
        NETWORK_ERROR -> {
          text = R.string.network_error_desc
          icon = R.drawable.ic_cloud_off_white_24dp
          color = R.color.red_200
        }
        CLIENT_ERROR -> {
          text = R.string.unknown_error_desc
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
        NOT_FOUND_ERROR -> {
          text = R.string.coin_not_found
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
        SERVER_ERROR -> {
          text = R.string.user_database_error
          icon = R.drawable.ic_dns_white_24dp
          color = R.color.red_200
        }
        UNAUTHORIZED_ERROR -> TODO()
        UNKNOWN_ERROR -> {
          text = R.string.unknown_error_desc
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
      }

      customToast(resources.getString(text), color, icon)
      requireActivity().onBackPressed()
    }
    viewModel.alertError.observe(viewLifecycleOwner) {
      isLoading = false

      var icon = 0
      var text = 0
      var color = 0
      when (it) {
        NOT_SAVED -> {
          text = R.string.alert_submitted
          icon = R.drawable.ic_cloud_off_white_24dp
          color = R.color.red_200
        }
        INVALID_ARGUMENTS -> {
          text = R.string.unknown_error_desc
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
        NO_ARGUMENTS -> {
          text = R.string.unknown_error_desc
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
        PriceAlertViewModel.AlertStatus.UNKNOWN_ERROR -> {
          text = R.string.unknown_error_desc
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          color = R.color.red_200
        }
        PriceAlertViewModel.AlertStatus.NETWORK_ERROR -> {
          text = R.string.network_error_desc
          icon = R.drawable.ic_cloud_off_white_24dp
          color = R.color.red_200
        }
        PriceAlertViewModel.AlertStatus.SERVER_ERROR -> {
          text = R.string.user_database_error
          icon = R.drawable.ic_dns_white_24dp
          color = R.color.red_200
        }
      }

      customToast(resources.getString(text), color, icon)
    }
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentInsertPriceAlertBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.txtIrt -> {
        if (!isIrt) {
          isIrt = true
          mPrice = coinModel?.priceTMN!!
          binding.edtPrice.setText(mPrice.toBigDecimal().toPlainString())
        }
      }
      R.id.txtUsd -> {
        if (isIrt) {
          isIrt = false
          mPrice = coinModel?.priceUSD!!
          binding.edtPrice.setText(mPrice.toBigDecimal().toPlainString())
        }
      }
      R.id.cardView -> {
        launch {
          withContext(Dispatchers.Main) {
            isLoading = true
          }
          val model = PriceAlertModel(
            mPrice,
            !isIrt,
            if (isIrt) mPrice > coinModel?.priceTMN!! else mPrice > coinModel?.priceUSD!!,
            (requireActivity() as MainActivity).userPrefs.getUserId(requireContext()),
            coinId
          )
          viewModel.submitAlert(model)
        }
      }
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}