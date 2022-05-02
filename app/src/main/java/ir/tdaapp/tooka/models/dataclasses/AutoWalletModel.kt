package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class AutoWalletModel(
  @SerializedName("api_key") var apiKey: String,
  @SerializedName("user_id") var userId: Int,
  @SerializedName("wallet_address") var walletAddress: String,
  @SerializedName("platform_id") var platformId: Int,
  @SerializedName("coin_id") var coinId: Int
)