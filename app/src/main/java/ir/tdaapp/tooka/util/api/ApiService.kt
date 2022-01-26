package ir.tdaapp.tooka.util.api

import ir.tdaapp.tooka.models.*
import ir.tdaapp.tooka.models.NobitexModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

  @GET("api/Home/GetData")
  fun getData(): Call<HomeContentResponse>

  @GET("api/Markets/GetAllCoins")
  fun getAllCoins(): Call<List<Coin>>

  @GET("api/Markets/GetSpecificCoinDetails")
  fun getSpecificCoin(@Query("id") id: Int): Call<CoinDetailsModel>

  @GET("api/Markets/GetOHLCV")
  fun getOHLCV(
    @Query("coinId") coinId: Int,
    @Query("timeFrameId") timeFrameId: Int
  ): Call<List<List<Any>>>

  @GET("api/Markets/GetRandomCoins")
  fun getRandomCoins(@Query("coinId") id: Int): Call<List<Coin>>

  @GET("api/News/GetAllNews")
  fun getNewsData(): Call<NewsContentResponse>

  @GET("api/News/GetSpecificNews")
  fun getSpecificNews(@Query("news_id") id: Int): Call<NewsDetailsModel>

  @GET("api/News/GetRelatedNews")
  fun getRelatedNews(@Query("coin_id") id: Int, @Query("count") count: Int): Call<List<News>>

  @GET("api/Markets/GetTest")
  fun getTest(@Query("news_id") id: Int): Call<String>

  @GET("api/Portfolio/DoesHavePortfolio")
  fun isPortfolioActivated(@Query("api_key") apiKey: String): Call<Boolean>

  @GET("api/Portfolio/GetAllCoins")
  fun getCoinsForPortfolio(): Call<List<Coin>>

  @GET("api/Portfolio/GetPlatforms")
  fun getPlatformsByCoin(@Query("coin_id") coinId: Int): Call<List<Platform>>

  @GET("api/Compare/CompareCoins")
  fun getCompareData(
    @Query("firstCoin") firstCoin: Int,
    @Query("secondCoin") secondCoin: Int
  ): Call<CompareModel>

  @GET("api/Convert/GetCoins")
  fun getCoinsToConvert(): Call<List<Coin>>

  @POST("api/Portfolio/AddAutoPortfolio")
  fun addPortfolio(@Body model: AutoWalletModel): Call<PostPortfolioResponse>

  @GET("api/Portfolio/GetAllBalances")
  fun getBalances(@Query("api_key") apiKey: String): Call<PortfolioInfo>

  @GET("api/Convert/ConvertPrice")
  fun getConvertData(
    @Query("firstId") firstCoin: Int,
    @Query("secondId") secondCoin: Int
  ): Call<ConvertModel>

  @GET("api/Search/SearchEverywhere")
  fun search(@Query("input") input: String):
    Call<SearchResponse>

  @GET("api/Notif/GetData")
  fun getNotifications(
    @Query("ApiKey") apiKey: String,
    @Query("Page") page: Int
  ):
    Call<List<Notification>>

  @GET("api/Compare/GetOHLCV")
  fun getCompareOHLCV(
    @Query("firstId") firstCoin: Int,
    @Query("secondId") secondCoin: Int,
    @Query("timeFrameId") timeFrameId: Int
  ): Call<CompareOHLCVModel>
}