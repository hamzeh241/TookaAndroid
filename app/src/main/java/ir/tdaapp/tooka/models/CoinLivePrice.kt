package ir.tdaapp.tooka.models


import com.google.gson.annotations.SerializedName

data class CoinLivePrice(
  @SerializedName("close_time")
  val closeTime: Long,
  @SerializedName("close_value")
  val closeValue: Double,
  @SerializedName("coin_id")
  val coinId: Int,
  @SerializedName("high_value")
  val highValue: Double,
  @SerializedName("low_value")
  val lowValue: Double,
  @SerializedName("open_time")
  val openTime: Long,
  @SerializedName("open_value")
  val openValue: Double,
  @SerializedName("tether_price")
  val tetherPrice: Double,
  @SerializedName("timeframe_id")
  val timeframeId: Int
)