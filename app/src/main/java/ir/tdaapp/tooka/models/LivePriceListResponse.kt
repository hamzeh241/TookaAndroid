package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class LivePriceListResponse(
  @SerializedName("id") var id:Int,
  @SerializedName("price_dollar") var priceUSD:Double,
  @SerializedName("price_tooman") var priceTMN:Double
)
