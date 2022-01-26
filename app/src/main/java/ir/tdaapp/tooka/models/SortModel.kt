package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class SortModel(
  @SerializedName("id") var id: Int,
  @SerializedName("name_en") var nameEn: String,
  @SerializedName("name_fa") var nameFa: String,
  var isSelected:Boolean,
  var isAscend:Boolean
)
