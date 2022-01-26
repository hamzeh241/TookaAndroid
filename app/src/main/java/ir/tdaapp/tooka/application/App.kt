package ir.tdaapp.tooka.application

import android.app.Application
import android.util.Log
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import ir.tdaapp.tooka.util.PreferenceHelper
import ir.tdaapp.tooka.util.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

  companion object {
    var preferenceHelper: PreferenceHelper? = null
  }

  private val DEFAULT_PREFERENCES = "default_preferences"

  override fun onCreate() {
    super.onCreate()

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
        listOf(appModule)
      )
    }
  }
}