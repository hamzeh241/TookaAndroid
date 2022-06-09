package ir.tdaapp.tooka.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.microsoft.signalr.HubConnection
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.signalr.SignalR
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val networkTimeout = 10L

val networkModule = module {
  factory { provideOkHttpClient(get()) }
  factory { provideGson() }
  factory { provideLogging() }
  single { provideTookaApi(get()) }
  single { provideRetrofit(get(), get()) }
  single { provideSignalR() }
}

private fun provideSignalR(): HubConnection = SignalR.hubConnection

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
    .callTimeout(networkTimeout, TimeUnit.SECONDS)
    .connectTimeout(networkTimeout, TimeUnit.SECONDS)
    .readTimeout(networkTimeout, TimeUnit.SECONDS)
    .writeTimeout(networkTimeout, TimeUnit.SECONDS)
    .build()
}

private fun provideTookaApi(retrofit: Retrofit): ApiService =
  retrofit.create(ApiService::class.java)