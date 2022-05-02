package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class CandleUpdateModel(
  @SerializedName("CoinId") var coinId: Int,
  @SerializedName("TimeframeId") var timeFrameId: Int,
  @SerializedName("OpenTime") var openTime: Long,
  @SerializedName("CloseTime") var closeTime: Long,
  @SerializedName("OpenValue") var openValue: Double,
  @SerializedName("HighValue") var highValue: Double,
  @SerializedName("LowValue") var lowValue: Double,
  @SerializedName("CloseValue") var closeValue: Double,
  @SerializedName("Volume") var volume: Double,
)