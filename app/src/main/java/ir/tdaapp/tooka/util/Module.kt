package ir.tdaapp.tooka.util

import ContextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.tdaapp.tooka.util.api.ApiService
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.viewmodels.*
import ir.tdaapp.tooka.views.fragments.HomeFragment
import ir.tdaapp.tooka.views.fragments.MarketsFragment
import ir.tdaapp.tooka.views.fragments.NewsFragment
import ir.tdaapp.tooka.views.fragments.PortfolioFragment
import ir.tdaapp.tooka.views.fragments.login.LoginFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
  single { ContextUtils(androidContext()) }
  single { RelatedNewsViewModel(get()) }
  single { RelatedCoinsViewModel(get()) }
  single { SharedViewModel(get()) }
  factory { NewsDetailsViewModel(get()) }
  factory { SearchViewModel(get()) }
}

val networkModule = module {
  factory { provideOkHttpClient(get()) }
  factory { provideGson() }
  factory { provideLogging() }
  single { provideTookaApi(get()) }
  single { provideRetrofit(get(), get()) }
}

val fragmentModule = module {
  single { HomeFragment() }
  single { MarketsFragment() }
  single { PortfolioFragment() }
  single { NewsFragment() }
  single { LoginFragment() }
}

val viewModelModule = module {
  single { HomeViewModel(get()) }
  single { MarketsViewModel(get()) }
  single { MainActivityViewModel() }
  single { LoginActivityViewModel(get()) }
  single { PortfolioViewModel(get()) }
  single { NewsViewModel(get()) }
  factory { NotificationsViewModel(get()) }
  factory { ManualBottomSheetViewModel(get()) }
  factory { AutomaticBottomSheetViewModel(get()) }
  factory { CoinDetailsViewModel(get()) }
  factory { PriceAlertViewModel(get()) }
  factory { PriceAlertListViewModel(get()) }
  factory { CoinsListViewModel(get()) }
  factory { AllNewsViewModel(get()) }
}

private fun provideLogging() = HttpLoggingInterceptor()
  .setLevel(HttpLoggingInterceptor.Level.BODY)

private fun provideGson() =
  GsonBuilder().setLenient().create()

private fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
  return Retrofit.Builder()
    .baseUrl(RetrofitClient.BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
}

private fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
  return OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
}

private fun provideTookaApi(retrofit: Retrofit): ApiService =
  retrofit.create(ApiService::class.java)