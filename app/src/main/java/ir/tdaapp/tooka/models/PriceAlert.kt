package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class PriceAlert(
  @SerializedName("id") var id: Int,
  @SerializedName("coin_name") var coinName: String,
  @SerializedName("coin_symbol") var coinSymbol: String,
  @SerializedName("coin_icon") var coinIcon: String,
  @SerializedName("price") var price: Double,
  @SerializedName("is_dollar") var isUsd: Boolean,
  @SerializedName("is_ascend") var isAscend: Boolean,
  @SerializedName("is_enabled") var isEnabled: Boolean,
  @SerializedName("coin_id") var coinId: Int,
  @SerializedName("create_date") var date: String
)