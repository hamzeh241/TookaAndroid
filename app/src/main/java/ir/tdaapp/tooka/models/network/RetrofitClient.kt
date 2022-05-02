package ir.tdaapp.tooka.models.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

  companion object {
    const val BASE_URL: String = "https://www.razhashop.ir/api/"

    const val NEWS_IMAGES: String = "https://www.razhashop.ir/images/news/"
    const val COIN_IMAGES: String = "https://www.razhashop.ir/images/coins/"

    const val NEWS_URL: String = "https://www.razhashop.ir/news/news"
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