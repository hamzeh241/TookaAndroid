package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class AutoWalletModel(
  @SerializedName("ApiKey") var apiKey: String,
  @SerializedName("UserId") var userId: Int,
  @SerializedName("WalletAddress") var walletAddress: String,
  @SerializedName("PlatformId") var platformId: Int,
  @SerializedName("CoinId") var coinId: Int
)