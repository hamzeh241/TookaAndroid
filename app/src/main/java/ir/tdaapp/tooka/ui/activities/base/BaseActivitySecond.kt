package ir.tdaapp.tooka.ui.activities.base

import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.models.preference.LanguagePreferences
import java.util.*


abstract class BaseActivitySecond: AppCompatActivity() {

  private val langPreference = LanguagePreferences()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(getLayout().root)
    init()
    initToolbar()
    initTransitions()
    initTheme()
  }

  fun getCurrentLocale(): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      resources.configuration.locales[0]
    } else {
      resources.configuration.locale
    }
  }

  abstract fun init()

  /**
   * Dar inja har Animation ya Transitioni ke baiad dar shoru'e app ejra shavad inja
   * neveshte mishavad
   * @see TransitionManager
   * @see Transition
   * @see Animation
   */
  abstract fun initTransitions()

  abstract fun initToolbar()

  abstract fun initLanguage()

  abstract fun initTheme()

  abstract fun getLayout(): ViewBinding
}