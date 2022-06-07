package ir.tdaapp.tooka.models.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "gainers_losers")
data class GainersLosers(
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
  @ColumnInfo(name = "rank")
  @SerializedName("rank") var rank: Int,
  @ColumnInfo(name = "percentage")
  @SerializedName("percentage")
  var percentage: Float
)