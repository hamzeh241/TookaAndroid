package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class SliderNews(
  @SerializedName("news_id") var newsId: Int,
  @SerializedName("title_en") var titleEn: String,
  @SerializedName("title_fa") var titleFa: String,
  @SerializedName("news_kind") val newsKind: Int,
  @SerializedName("url") val newsUrl: String,
  @SerializedName("image_url") var imageUrl: String
)
