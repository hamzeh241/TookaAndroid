package ir.tdaapp.tooka.ui.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogManualBottomSheetBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.ManualWalletModel
import ir.tdaapp.tooka.models.enums.ManualPortfolioErrors
import ir.tdaapp.tooka.models.util.isLoading
import ir.tdaapp.tooka.models.viewmodels.ManualBottomSheetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class ManualPortfolioBottomSheetDialog: BottomSheetDialogFragment(), CoroutineScope,
  View.OnClickListener {

  companion object {
    const val TAG = "ManualPortfolioBottomSheetDialog"
  }

  interface ManualPortfolioDialogCallback {
    fun onResult()
    fun onError(error: ManualPortfolioErrors)
  }

  var callback: ManualPortfolioDialogCallback? = null

  private lateinit var binding: DialogManualBottomSheetBinding

  private var selectedCoin: Coin? = null
  private var coins: ArrayList<Coin>? = null

  private val viewModel: ManualBottomSheetViewModel by inject()
  private var isBought = true

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DialogManualBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    lifecycleScope.launchWhenStarted {
      viewModel.getData()
    }

    binding.autoCompleteSpinner.onItemClickListener =
      object: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          binding.textInputCoins.error = null
          launch {
            selectedCoin = coins?.get(position)
          }
        }
      }

    binding.textInputEditText.doOnTextChanged { text, start, before, count ->
      if (text?.length!! > 0)
        binding.textInputCapital.error = null
    }

    binding.cardSell.setOnClickListener(this)
    binding.cardBuy.setOnClickListener(this)
    binding.submit.setOnClickListener(this)

    binding.textInputCoins.startIconDrawable
    viewModel.coins.observe(viewLifecycleOwner, {
      coins = it as ArrayList<Coin>
      val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
      (binding.autoCompleteSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    })
    viewModel.error.observe(viewLifecycleOwner) {
      val message: String
      @DrawableRes val imageRes: Int
      when (it) {
        ManualPortfolioErrors.NO_ARGS -> {
          message = getString(R.string.unknown_error_desc)
          imageRes = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        ManualPortfolioErrors.INVALID_ARGS -> {
          message = getString(R.string.unknown_error_desc)
          imageRes = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        ManualPortfolioErrors.NOT_SAVED -> {
          message = getString(R.string.unknown_error_desc)
          imageRes = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        ManualPortfolioErrors.NETWORK_ERROR -> {
          message = getString(R.string.network_error_desc)
          imageRes = R.drawable.ic_dns_white_24dp
        }
        ManualPortfolioErrors.SERVER_ERROR -> {
          message = getString(R.string.server_error_desc)
          imageRes = R.drawable.ic_dns_white_24dp
        }
        ManualPortfolioErrors.UNKNOWN_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          imageRes = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
      }
      val toastBinding = ToastLayoutBinding.inflate(layoutInflater).apply {
        this.message.text = message
        image.setImageResource(imageRes)
      }
      Toast(requireContext()).apply {
        setDuration(Toast.LENGTH_LONG)
        setView(toastBinding.root)
        show()
      }

      binding.txtSubmit.isLoading(false)
      callback!!.onError(it)
      dismiss()
    }

    viewModel.postResult.observe(viewLifecycleOwner) {
      binding.txtSubmit.isLoading(false)
      callback!!.onResult()
      dismiss()
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.cardBuy -> {
        if (!isBought) {
          isBought = true
          binding.textInputCoins.hint = getString(R.string.crypto_bought)
          binding.textInputCapital.hint = getString(R.string.amout_of_crypto_bought)
          binding.cardSell.setCardBackgroundColor(Color.WHITE)
          binding.cardBuy.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        }
      }
      R.id.cardSell -> {
        if (isBought) {
          isBought = false
          binding.textInputCoins.hint = getString(R.string.crypto_sold)
          binding.textInputCapital.hint = getString(R.string.amount_of_crypto_sold)
          binding.cardBuy.setCardBackgroundColor(Color.WHITE)
          binding.cardSell.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
        }
      }
      R.id.submit -> {
        if (selectedCoin == null) {
          binding.textInputCoins.error = getString(R.string.select_coin)

        } else if (binding.textInputEditText.text.toString().isBlank()
          || binding.textInputEditText.text.toString()
            .equals(".") || binding.textInputEditText.text.toString().toDouble() <= 0
        ) {
          binding.textInputCapital.error = getString(R.string.specify_capital)
        } else {
          binding.txtSubmit.isLoading(true)
          launch(Dispatchers.Main) {
            val model = ManualWalletModel(
              "apikey",
              (requireActivity() as MainActivity).userPrefs.getUserId(),
              selectedCoin!!.id,
              2,
              binding.textInputEditText.text.toString().toDouble(),
              isBought
            )
            viewModel.postData(model)
          }
        }
      }
    }
  }
}