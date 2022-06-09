package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class ManualWalletModel(
  @SerializedName("ApiKey") var apiKey: String,
  @SerializedName("UserId") var userId: Int,
  @SerializedName("CoinId") var coinId: Int,
  @SerializedName("Kind") var kind: Int,
  @SerializedName("Capital") var capital: Double,
  @SerializedName("IsBought") var isBought: Boolean,
)