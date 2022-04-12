package ir.tdaapp.tooka.application

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import ir.tdaapp.tooka.BuildConfig
import ir.tdaapp.tooka.util.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.*

class App: Application() {

  companion object {
    var preferenceHelper: PreferenceHelper? = null
  }

  private val DEFAULT_PREFERENCES = "default_preferences"

  val langPreferences = LanguagePreferences()

  override fun onCreate() {
    super.onCreate()

    LocaleHelper.setLocale(Locale(langPreferences.getLang(this)!!))
    LocaleHelper.updateConfig(this, getBaseContext().getResources().getConfiguration())

    if (BuildConfig.DEBUG)
      Timber.plant(Timber.DebugTree())

    FlurryAgent.Builder()
      .withCaptureUncaughtExceptions(true)
      .withIncludeBackgroundSessionsInMetrics(true)
      .withLogLevel(Log.VERBOSE)
      .withPerformanceMetrics(FlurryPerformance.ALL)
      .build(this, "QDTDTXN39BFBYVWHPDHF")

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