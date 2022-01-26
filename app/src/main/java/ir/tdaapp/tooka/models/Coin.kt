package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName
import java.lang.StringBuilder

data class Coin(
  @SerializedName("id") var id: Int,
  @SerializedName("coin_name") var name: String,
  @SerializedName("coin_persian_name") var persianName: String?,
  @SerializedName("price_dollar") var priceUSD: Double,
  @SerializedName("price_toman") var priceTMN: Double,
  @SerializedName("coin_symbol") var symbol: String,
  @SerializedName("nobitex_id") var nobitexId: String,
  @SerializedName("coin_icon") var icon: String,
  @SerializedName("ohlc") var ohlc: List<Double>,
  @SerializedName("rank") var rank: Int,
  @SerializedName("percentage") var percentage: Float,
  @SerializedName("is_watchlist") var isWatchlist: Boolean,
  var viewType: Int,
  var isStarred: Boolean
) {
  override fun toString(): String {
    return when (persianName != null) {
      true -> StringBuilder(name)
        .append(" (").append(persianName)
        .append(")").toString()
      else -> name
    }
  }
}
