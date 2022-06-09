package ir.tdaapp.tooka

import android.app.Application
import com.flurry.android.FlurryAgent
import ir.tdaapp.tooka.di.dataSourceModule
import ir.tdaapp.tooka.di.networkModule
import ir.tdaapp.tooka.di.repositoryModule
import ir.tdaapp.tooka.di.viewModelModule
import ir.tdaapp.tooka.models.preference.LanguagePreferences
import ir.tdaapp.tooka.models.preference.ThemePreference
import ir.tdaapp.tooka.models.preference.TokenPreferences
import ir.tdaapp.tooka.models.util.PreferenceHelper
import ir.tdaapp.tooka.models.util.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App: Application() {

  lateinit var preferenceHelper: PreferenceHelper
  lateinit var themePreference: ThemePreference
  lateinit var langPreferences: LanguagePreferences
  private val DEFAULT_PREFERENCES = "default_preferences"

  val tokenPreferences = TokenPreferences()

  override fun onCreate() {
    super.onCreate()

    themePreference = ThemePreference(this)
    langPreferences = LanguagePreferences(this)

    preferenceHelper = PreferenceHelper(
      getSharedPreferences(DEFAULT_PREFERENCES, MODE_PRIVATE),
      applicationContext
    )

    FlurryAgent.Builder()
      .withLogEnabled(true)
      .build(this, "KQB9KQ4H8KJ9H6QP3GPR")

    if (BuildConfig.DEBUG)
      Timber.plant(Timber.DebugTree())


    startKoin {
      androidLogger(Level.DEBUG)
      androidContext(this@App)
      modules(
        listOf(
          appModule, viewModelModule, networkModule, repositoryModule,
          dataSourceModule
        )
      )
    }
  }
}