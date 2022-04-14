package ir.tdaapp.tooka.application

import android.app.Application
import android.content.res.Configuration
import ir.tdaapp.tooka.BuildConfig
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.preference.LanguagePreferences
import ir.tdaapp.tooka.util.preference.TokenPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.*

class App: Application() {

  lateinit var preferenceHelper: PreferenceHelper
  private val DEFAULT_PREFERENCES = "default_preferences"

  val langPreferences = LanguagePreferences()
  val tokenPreferences = TokenPreferences()

  override fun onCreate() {
    super.onCreate()

    LocaleHelper.setLocale(Locale(langPreferences.getLang(this)!!))
    LocaleHelper.updateConfig(this, getBaseContext().getResources().getConfiguration())

    if (BuildConfig.DEBUG)
      Timber.plant(Timber.DebugTree())

    preferenceHelper = PreferenceHelper(
      getSharedPreferences(DEFAULT_PREFERENCES, MODE_PRIVATE),
      applicationContext
    )

    startKoin {
      androidLogger(Level.DEBUG)
      androidContext(this@App)
      modules(
        listOf(appModule, viewModelModule, fragmentModule, networkModule)
      )
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    LocaleHelper.updateConfig(this, newConfig);
  }
}