package ir.tdaapp.tooka.views.activities.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ir.tdaapp.tooka.util.LocaleHelper
import ir.tdaapp.tooka.util.preference.LanguagePreferences


open class BaseActivity: AppCompatActivity() {

  companion object {
    const val TAG = "BaseActivity"
  }

  override fun attachBaseContext(newBase: Context?) {
    if (newBase != null) {
      val prefs = LanguagePreferences()
      val lang = prefs.getLang(newBase)
      super.attachBaseContext(LocaleHelper.localeUpdateResources(newBase, lang))
    } else super.attachBaseContext(newBase)
  }
}