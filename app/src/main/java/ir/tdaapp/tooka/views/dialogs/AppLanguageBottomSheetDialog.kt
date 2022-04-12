package ir.tdaapp.tooka.views.dialogs

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

typealias Callback = (AppLanguageBottomSheetDialog.Language) -> Unit

class AppLanguageBottomSheetDialog(val callback:Callback): BottomSheetDialogFragment() {

  enum class Language {
    ENGLISH,
    PERSIAN
  }

  companion object {
    const val TAG = "AppLanguageBottomSheetDialog"
  }
}