package ir.tdaapp.tooka.util.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {

  companion object {
    const val BASE_URL: String = "https://www.razhashop.ir/"

    const val NEWS_IMAGES: String = "https://www.razhashop.ir/Images/News/"
    const val COIN_IMAGES: String = "https://www.razhashop.ir/Images/Coins/"


    const val SERVER_ERROR: Int = 500
    const val BAD_REQUEST: Int = 400
    const val NOT_FOUND: Int = 404
  }

  var logging = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

  val client: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .addInterceptor(logging)
      .build()
  }

  private val gson: Gson by lazy {
    GsonBuilder().setLenient().create()
  }

  private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  val service: ApiService by lazy {
    retrofit.create(ApiService::class.java)
  }
}