package ir.tdaapp.tooka.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.network.RetrofitClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
  factory { provideOkHttpClient(get()) }
  factory { provideGson() }
  factory { provideLogging() }
  single { provideTookaApi(get()) }
  single { provideRetrofit(get(), get()) }
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