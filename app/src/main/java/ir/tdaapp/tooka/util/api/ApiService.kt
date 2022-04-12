package ir.tdaapp.tooka.util.api

import ir.tdaapp.tooka.models.*
import retrofit2.Call
import retrofit2.Response
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


  @GET("api/Portfolio/GetAllBalances")
  fun getBalances(@Query("api_key") apiKey: String): Call<PortfolioInfo>

  @GET("api/Convert/ConvertPrice")
  fun getConvertData(
    @Query("firstId") firstCoin: Int,
    @Query("secondId") secondCoin: Int
  ): Call<ConvertModel>

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

  @GET("home/data")
  suspend fun homeData(): Response<ResponseModel<HomeContentResponse>>

  @GET("markets/allcoins")
  suspend fun allCoins(
    @Query("ascend") ascend: Boolean,
    @Query("userId") userId: Int = 0,
    @Query("sortOptions") sortOptions: Int = 0
  ): Response<ResponseModel<List<Coin>>>

  @GET("markets/sortoptions")
  suspend fun sortOptions(): Response<ResponseModel<List<SortModel>>>

  @GET("portfolio/balances")
  suspend fun allBalances(@Query("UserId") userId: Int): Response<ResponseModel<PortfolioInfo>>

  @GET("news/data")
  suspend fun newsData(): Response<ResponseModel<NewsContentResponse>>

  @POST("user/auth")
  suspend fun loginOrSignup(@Query("PhoneNumber") phone: String): Response<ResponseModel<Boolean>>

  @POST("user/resend")
  suspend fun resendSms(@Query("PhoneNumber") phone: String): Response<ResponseModel<Boolean>>

  @POST("user/googleauth")
  suspend fun loginOrSignupGoogle(@Body phone: GoogleLoginModel): Response<ResponseModel<Boolean>>

  @POST("user/verify")
  suspend fun verify(@Body model: LoginModel): Response<ResponseModel<UserInfoResponse>>

  @GET("markets/coindetails")
  suspend fun coinDetails(@Query("coinId") coinId: Int): Response<ResponseModel<CoinDetailsModel>>

  @GET("markets/addwatchlist")
  suspend fun addWatchlist(
    @Query("coinId") coinId: Int,
    @Query("userId") userId: Int
  ): Response<ResponseModel<Boolean>>

  @GET("markets/randomcoins")
  suspend fun randomCoins(
    @Query("coinId") coinId: Int,
    @Query("count") count: Int = 0
  ): Response<ResponseModel<List<Coin>>>

  @GET("news/relatednews")
  suspend fun relatedNews(
    @Query("coinId") coinId: Int,
    @Query("count") count: Int = 0
  ): Response<ResponseModel<List<News>>>

  @GET("markets/ohlcv")
  suspend fun ohlcv(
    @Query("coinId") coinId: Int,
    @Query("timeframeId") timeFrameId: Int
  ): Response<ResponseModel<List<List<Any>>>>

  @GET("markets/irtohlcv")
  suspend fun irtOhlcv(
    @Query("coinId") coinId: Int,
    @Query("timeframeId") timeFrameId: Int
  ): Response<ResponseModel<List<List<Any>>>>

  @GET("markets/timeframes")
  suspend fun timeFrames(
    @Query("coinId") coinId: Int
  ): Response<ResponseModel<List<TimeFrameModel>>>

  @GET("alert/price")
  suspend fun coinPrice(@Query("coinId") coinId: Int): Response<ResponseModel<LivePriceListResponse>>

  @POST("alert/submit")
  suspend fun submitAlert(
    @Body model: PriceAlertModel
  ): Response<ResponseModel<Boolean>>

  @GET("alert/alerts")
  suspend fun alertList(
    @Query("userId") userId: Int
  ): Response<ResponseModel<List<PriceAlert>>>

  @PUT("alert/toggle")
  suspend fun toggleAlert(
    @Query("userId") userId: Int,
    @Query("alertId") alertId: Int
  ): Response<ResponseModel<Boolean>>

  @DELETE("alert/delete")
  suspend fun deleteAlert(
    @Query("userId") userId: Int,
    @Query("alertId") alertId: Int
  ): Response<ResponseModel<Boolean>>

  @GET("markets/mutualtimeframes")
  suspend fun mutualTimeFrames(
    @Query("firstId") firstId: Int,
    @Query("secondId") secondId: Int
  ): Response<ResponseModel<List<TimeFrameModel>>>

  @GET("portfolio/autowalletcoins")
  suspend fun autoWalletCoins(): Response<ResponseModel<List<Coin>>>

  @GET("portfolio/platforms")
  suspend fun platformsByCoin(@Query("coinId") coinId: Int): Response<ResponseModel<List<Platform>>>

  @POST("portfolio/addautoportfolio")
  suspend fun addAutoPortfolio(@Body model: AutoWalletModel): Response<ResponseModel<Boolean>>

  @POST("portfolio/addmanualportfolio")
  suspend fun addManualPortfolio(@Body model: ManualWalletModel): Response<ResponseModel<Boolean>>

  @GET("user/notif")
  suspend fun notifications(@Query("userId") userId: Int = 0): Response<ResponseModel<List<Notification>>>

  @GET("search/everywhere")
  suspend fun search(@Query("input") input: String):
    Response<ResponseModel<SearchResponse>>
}
