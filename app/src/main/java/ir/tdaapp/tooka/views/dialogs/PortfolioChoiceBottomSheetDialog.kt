package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.databinding.DialogPortfolioChoiceBottomSheetBinding

class PortfolioChoiceBottomSheetDialog: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "PortfolioBottomSheetDialog"
  }

  interface PortfolioChoiceBottomSheetCallback {
    fun onAutomaticSelected()
    fun onManualSelected()
  }

  lateinit var binding: DialogPortfolioChoiceBottomSheetBinding
  var callback: PortfolioChoiceBottomSheetCallback? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogPortfolioChoiceBottomSheetBinding.inflate(
      LayoutInflater.from(context),
      container,
      false
    )

    binding.automaticChoice.setOnClickListener {
      callback!!.onAutomaticSelected()
      dismiss()
    }

    binding.manualChoice.setOnClickListener {
      callback!!.onManualSelected()
      dismiss()
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.automaticChoice.setOnClickListener {
      callback!!.onAutomaticSelected()
      dismiss()
    }

    binding.manualChoice.setOnClickListener {
      callback!!.onManualSelected()
      dismiss()
    }
  }
}