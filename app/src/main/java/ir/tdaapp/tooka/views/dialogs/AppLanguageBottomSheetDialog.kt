package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.databinding.DialogAppLanguageBottomSheetBinding

typealias Callback = (AppLanguageBottomSheetDialog.Language)->Unit

class AppLanguageBottomSheetDialog(val callback: Callback): BottomSheetDialogFragment() {

  enum class Language {
    ENGLISH,
    PERSIAN
  }

  companion object {
    const val TAG = "AppLanguageBottomSheetDialog"
  }

  private lateinit var binding: DialogAppLanguageBottomSheetBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogAppLanguageBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.ctvEnglish.setOnClickListener {
      callback(Language.ENGLISH)
      dismiss()
    }

    binding.ctvPersian.setOnClickListener {
      callback(Language.PERSIAN)
      dismiss()
    }
  }
}