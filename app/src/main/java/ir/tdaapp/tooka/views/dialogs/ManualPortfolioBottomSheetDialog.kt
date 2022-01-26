package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogManualBottomSheetBinding
import ir.tdaapp.tooka.viewmodels.ManualBottomSheetViewModel
import org.koin.android.ext.android.inject

class ManualPortfolioBottomSheetDialog: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "ManualPortfolioBottomSheetDialog"
  }

  interface ManualPortfolioDialogCallback {
    fun onResult()
  }

  var callback: ManualPortfolioDialogCallback? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  lateinit var binding: DialogManualBottomSheetBinding

  val viewModel: ManualBottomSheetViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogManualBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initializeToggleButton()
    viewModel.getData()
    viewModel.coins.observe(viewLifecycleOwner, {
      val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
      (binding.autoCompleteSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    })
  }

  fun initializeToggleButton() {
    binding.tookaToggleButton.setFirstChoiceText(getString(R.string.buy))
    binding.tookaToggleButton.setSecondChoiceText(getString(R.string.sell))
    binding.tookaToggleButton.setFirstChoiceIcon(0)
    binding.tookaToggleButton.setSecondChoiceIcon(0)
    binding.tookaToggleButton.setCallback {
      when (it) {
        1 -> {
          binding.textInputCoins.hint = getString(R.string.crypto_bought)
          binding.textInputCapital.hint = getString(R.string.amout_of_crypto_bought)
        }
        2 -> {
          binding.textInputCoins.hint = getString(R.string.crypto_sold)
          binding.textInputCapital.hint = getString(R.string.amount_of_crypto_sold)
        }
      }
    }
  }
}