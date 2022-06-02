package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class Coin(
  @SerializedName("id") var id: Int,
  @SerializedName("coin_name") var name: String,
  @SerializedName("coin_persian_name") var persianName: String?,
  @SerializedName("price_dollar") var priceUSD: Double,
  @SerializedName("price_toman") var priceTMN: Double,
  @SerializedName("coin_symbol") var symbol: String,
  @SerializedName("nobitex_id") var nobitexId: String?,
  @SerializedName("coin_icon") var icon: String,
  @SerializedName("ohlc") var ohlc: List<Double>,
  @SerializedName("rank") var rank: Int,
  @SerializedName("percentage") var percentage: Float,
  @SerializedName("is_watchlist") var isWatchlist: Boolean,
  var viewType: Int,
  var isStarred: Boolean
) {

  override fun equals(other: Any?): Boolean {
    if (javaClass != other?.javaClass)
      return false

    other as Coin

    if (id != other.id)
      return false
    else if(name != other.name)
      return false
    else if(persianName != other.persianName)
      return false
    else if(priceUSD != other.priceUSD)
      return false
    else if(priceTMN != other.priceTMN)
      return false
    else if(symbol != other.symbol)
      return false
    else if(nobitexId != other.nobitexId)
      return false
    else if(icon != other.icon)
      return false
    else if(ohlc != other.ohlc)
      return false
    else if(rank != other.rank)
      return false
    else if(percentage != other.percentage)
      return false
    else if(isWatchlist != other.isWatchlist)
      return false
    else if(isStarred != other.isStarred)
      return false

    return true
  }

  override fun toString(): String {
    return when (persianName != null) {
      true -> StringBuilder(name)
        .append(" (").append(persianName)
        .append(")").toString()
      else -> name
    }
  }
}