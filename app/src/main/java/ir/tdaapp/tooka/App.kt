package ir.tdaapp.tooka

import com.zeugmasolutions.localehelper.LocaleAwareApplication
import ir.tdaapp.tooka.di.dataSourceModule
import ir.tdaapp.tooka.di.networkModule
import ir.tdaapp.tooka.di.repositoryModule
import ir.tdaapp.tooka.di.viewModelModule
import ir.tdaapp.tooka.models.preference.LanguagePreferences
import ir.tdaapp.tooka.models.preference.TokenPreferences
import ir.tdaapp.tooka.models.util.PreferenceHelper
import ir.tdaapp.tooka.models.util.appModule
import ir.tdaapp.tooka.models.util.fragmentModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App: LocaleAwareApplication() {

  lateinit var preferenceHelper: PreferenceHelper
  private val DEFAULT_PREFERENCES = "default_preferences"

  val langPreferences = LanguagePreferences()
  val tokenPreferences = TokenPreferences()


  override fun onCreate() {
    super.onCreate()

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
        listOf(
          appModule, viewModelModule, fragmentModule, networkModule, repositoryModule,
          dataSourceModule
        )
      )
    }
  }
}