package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class ManualWalletModel(
  @SerializedName("api_key") var apiKey: String,
  @SerializedName("user_id") var userId: Int,
  @SerializedName("coin_id") var coinId: Int,
  @SerializedName("capital") var capital: Double,
  @SerializedName("is_bought") var isBought: Boolean,
)
