package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class NewsDetailsModel(
  @SerializedName("news_id") val id: Int,
  @SerializedName("news_title_en") val titleEn: String,
  @SerializedName("news_title_fa") val titleFa: String,
  @SerializedName("news_content_en") val contentEn: String,
  @SerializedName("news_content_fa") val contentFa: String,
  @SerializedName("news_short_content_en") val shortContentEn: String,
  @SerializedName("news_short_content_fa") val shortContentFa: String,
  @SerializedName("news_image_url") val imageUrl: String,
  @SerializedName("news_kind") val newsKind: Int,
  @SerializedName("news_write_date") val writeDate: String,
  @SerializedName("news_url") val url: String,
  @SerializedName("author") val author: Author
) {

  data class Author(
    @SerializedName("author_id") var id: Int,
    @SerializedName("author_name_fa") var persianName: String,
    @SerializedName("author_name_en") var name: String
  )

}