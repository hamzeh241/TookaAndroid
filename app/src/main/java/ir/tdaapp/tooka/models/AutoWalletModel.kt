package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class AutoWalletModel(
  @SerializedName("api_key") var apiKey: String,
  @SerializedName("user_id") var userId: Int,
  @SerializedName("wallet_address") var walletAddress: String,
  @SerializedName("coin_id") var coinId: Int
)
