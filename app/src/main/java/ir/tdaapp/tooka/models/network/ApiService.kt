package ir.tdaapp.tooka.models.network

import ir.tdaapp.tooka.models.dataclasses.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

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

  @GET("news/allnews")
  suspend fun allNews(@Query("page") page: Int = 0): Response<ResponseModel<List<News>>>
}
