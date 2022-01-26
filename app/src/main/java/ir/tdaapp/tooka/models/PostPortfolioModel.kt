package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class PostPortfolioModel(
  @SerializedName("api_key") var apiKey : String,
  @SerializedName("user_id") var userId : Int,
  @SerializedName("kind") var kind : Int,
  @SerializedName("capital") var capital : Double,
  @SerializedName("wallet_address") var walletAddress : String,
  @SerializedName("coin_id") var coinId : Int
)
