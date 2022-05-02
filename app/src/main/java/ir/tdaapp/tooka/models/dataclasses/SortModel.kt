package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class SortModel(
  @SerializedName("id") var id: Int,
  @SerializedName("name_en") var nameEn: String,
  @SerializedName("name_fa") var nameFa: String,
  var isSelected:Boolean,
  var isAscend:Boolean
){
  override fun equals(other: Any?): Boolean {

    if (javaClass != other?.javaClass)
      return false

    other as SortModel
    if (id != other.id)
      return false
    else if (nameEn != other.nameEn)
      return false
    else if (nameFa != other.nameFa)
      return false
    else if (isSelected != other.isSelected)
      return false
    else if (isAscend != other.isAscend)
      return false

    return true
  }
}