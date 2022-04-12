package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class PriceAlertModel(
  @SerializedName("Price") var price: Double,
  @SerializedName("IsDollar") var isDollar: Boolean,
  @SerializedName("IsAscend") var isAscend: Boolean,
  @SerializedName("UserId") var userId: Int,
  @SerializedName("CoinId") var coinId: Int
)