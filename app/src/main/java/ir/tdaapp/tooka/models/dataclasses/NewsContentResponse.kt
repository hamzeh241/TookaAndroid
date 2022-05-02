package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class NewsContentResponse(
  @SerializedName("slider_news") val sliderNews: List<SliderNews>,
  @SerializedName("breaking_news") val breakingNews: List<News>,
  @SerializedName("all_news") val allNews: List<News>
  )