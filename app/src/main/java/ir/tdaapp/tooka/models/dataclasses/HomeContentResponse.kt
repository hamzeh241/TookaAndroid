package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class HomeContentResponse(
  @SerializedName("top_coins") var topCoins: List<Coin>,
  @SerializedName("important_news") var breakingNews: List<News>,
  @SerializedName("gainers_losers") var gainersLosers: List<Coin>,
  @SerializedName("watchlist") var watchlist: List<Coin>
)