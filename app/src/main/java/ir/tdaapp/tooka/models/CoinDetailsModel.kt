package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class CoinDetailsModel(
  @SerializedName("coin_name") var name: String,
  @SerializedName("coin_persian_name") var persianName: String,
  @SerializedName("price_dollar") var priceUSD: Double,
  @SerializedName("price_toman") var priceTMN: Double,
  @SerializedName("percentage") var percentage: Double,
  @SerializedName("coin_symbol") var symbol: String,
  @SerializedName("nobitex_id") var nobitexId: String,
  @SerializedName("coin_icon") var icon: String,
  @SerializedName("statistics") var stats: Statistics,
  @SerializedName("related_news") var relatedNews: List<News>,
  @SerializedName("time_frames") var timeFrames: List<TimeFrameModel>
) {
  data class Statistics(
    @SerializedName("coin_id") var id: Int,
    @SerializedName("stat_market_cap") var marketCap: String,
    @SerializedName("stat_circulating_supply") var circSupply: String,
    @SerializedName("stat_total_supply") var totalSupply: String,
    @SerializedName("stat_rank") var rank: Int,
    @SerializedName("stat_24h_volume") var vol24: String,
    @SerializedName("stat_max_supply") var maxSupply: String,
    @SerializedName("stat_roi") var roi: String,
    @SerializedName("create_date") var createDate: String,
  )
}