package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("coins")
    val coins: List<Coin>,
    @SerializedName("news")
    val news: List<News>
)