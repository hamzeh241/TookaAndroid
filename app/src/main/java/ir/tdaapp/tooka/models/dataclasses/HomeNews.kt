package ir.tdaapp.tooka.models.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_news")
data class HomeNews(
  @ColumnInfo(name = "author_id") val author_id: Int,
  @ColumnInfo(name = "author_name_en") val author_name_en: String,
  @ColumnInfo(name = "author_name_fa") val author_name_fa: String,
  @PrimaryKey @ColumnInfo(name = "news_id") val news_id: Int,
  @ColumnInfo(name = "news_image_url") val news_image_url: String,
  @ColumnInfo(name = "news_kind") val news_kind: Int,
  @ColumnInfo(name = "news_title_en") val news_title_en: String,
  @ColumnInfo(name = "news_title_fa") val news_title_fa: String,
  @ColumnInfo(name = "news_url") val news_url: String?,
  @ColumnInfo(name = "news_write_date") val news_write_date: String
)