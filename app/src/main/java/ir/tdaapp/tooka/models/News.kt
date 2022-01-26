package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class News(
  @SerializedName("news_id") val id: Int,
  @SerializedName("news_title_en") val titleEn: String,
  @SerializedName("news_title_fa") val titleFa: String,
  @SerializedName("news_content_en") val contentEn: String,
  @SerializedName("news_content_fa") val contentFa: String,
  @SerializedName("news_short_content_en") val shortContentEn: String,
  @SerializedName("news_short_content_fa") val shortContentFa: String,
  @SerializedName("news_image_url") val imageUrl: String,
  @SerializedName("author_name_en") val authorNameEn: String,
  @SerializedName("author_name_fa") val authorNameFa: String,
  @SerializedName("author_id") val authorId: Int,
  @SerializedName("news_kind") val newsKind: Int,
  @SerializedName("create_date") val writeDate: String,
  @SerializedName("news_url") val url: String
){

  companion object{
    const val OPEN_URL = 1
    const val SHORT_NEWS = 2
    const val LONG_NEWS = 3
    const val SLIDER_NEWS = 4
  }

}