package ir.tdaapp.tooka.ui.activities.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ir.tdaapp.tooka.models.preference.LanguagePreferences
import ir.tdaapp.tooka.models.preference.ThemePreference
import ir.tdaapp.tooka.models.util.LocaleChanger
import java.util.*


open class BaseActivity: AppCompatActivity() {

  companion object {
    const val TAG = "BaseActivity"
  }

  lateinit var themePreferences: ThemePreference

  override fun attachBaseContext(newBase: Context) {
    val helper = LanguagePreferences(newBase)
    val localeToSwitchTo = Locale(helper.getLang())
    val localeUpdatedContext = LocaleChanger.updateLocale(newBase, localeToSwitchTo)
    super.attachBaseContext(localeUpdatedContext);
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    themePreferences = ThemePreference(this)
    themePreferences.isNightMode().let {
      if (it) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
      }
    }
  }
}