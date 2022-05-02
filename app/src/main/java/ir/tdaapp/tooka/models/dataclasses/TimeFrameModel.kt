package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class TimeFrameModel(
  @SerializedName("id") var id: Int,
  @SerializedName("number_caption") var num: String,
  @SerializedName("caption_en") var captionEN: String,
  @SerializedName("caption_fa") var captionFa: String,
  var isSelected: Boolean
)