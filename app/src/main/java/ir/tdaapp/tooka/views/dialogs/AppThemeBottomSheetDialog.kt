package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.databinding.DialogAppThemeBottomSheetBinding

class AppThemeBottomSheetDialog: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "AppThemeBottomSheetDial"
  }

  lateinit var binding: DialogAppThemeBottomSheetBinding

  interface AppThemeBottomSheetCallback {
    fun onLightModeClicked()
    fun onDarkModeClicked()
  }

  var callback: AppThemeBottomSheetCallback? = null
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
    binding = DialogAppThemeBottomSheetBinding.inflate(LayoutInflater.from(context), container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.ctvLightMode.setOnClickListener {
      callback!!.onLightModeClicked()
      dismiss()
    }

    binding.ctvNightMode.setOnClickListener {
      callback!!.onDarkModeClicked()
      dismiss()
    }
  }
}