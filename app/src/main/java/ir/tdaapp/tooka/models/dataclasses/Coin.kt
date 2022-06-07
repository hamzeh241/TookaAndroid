package ir.tdaapp.tooka.models.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "coins")
data class Coin(
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  var id: Int,
  @ColumnInfo(name = "coin_name")
  @SerializedName("coin_name")
  var name: String,
  @ColumnInfo(name = "coin_persian_name")
  @SerializedName("coin_persian_name")
  var persianName: String?,
  @ColumnInfo(name = "price_dollar")
  @SerializedName("price_dollar")
  var priceUSD: Double,
  @ColumnInfo(name = "price_toman")
  @SerializedName("price_toman")
  var priceTMN: Double,
  @ColumnInfo(name = "coin_symbol")
  @SerializedName("coin_symbol")
  var symbol: String,
  @ColumnInfo(name = "nobitex_id")
  @SerializedName("nobitex_id")
  var nobitexId: String?,
  @ColumnInfo(name = "coin_icon")
  @SerializedName("coin_icon")
  var icon: String,
  @ColumnInfo(name = "ohlc")
  @SerializedName("ohlc")
  var ohlc: List<Double>,
  @ColumnInfo(name = "rank")
  @SerializedName("rank")
  var rank: Int,
  @ColumnInfo(name = "percentage")
  @SerializedName("percentage")
  var percentage: Float,
  @ColumnInfo(name = "is_watchlist")
  @SerializedName("is_watchlist")
  var isWatchlist: Boolean,
  var viewType: Int,
  var isStarred: Boolean
) {

  override fun equals(other: Any?): Boolean {
    if (javaClass != other?.javaClass)
      return false

    other as Coin

    if (id != other.id)
      return false
    else if (name != other.name)
      return false
    else if (persianName != other.persianName)
      return false
    else if (priceUSD != other.priceUSD)
      return false
    else if (priceTMN != other.priceTMN)
      return false
    else if (symbol != other.symbol)
      return false
    else if (nobitexId != other.nobitexId)
      return false
    else if (icon != other.icon)
      return false
    else if (ohlc != other.ohlc)
      return false
    else if (rank != other.rank)
      return false
    else if (percentage != other.percentage)
      return false
    else if (isWatchlist != other.isWatchlist)
      return false
    else if (isStarred != other.isStarred)
      return false

    return true
  }

  override fun toString(): String = name
}