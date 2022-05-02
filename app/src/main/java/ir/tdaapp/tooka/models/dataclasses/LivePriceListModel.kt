package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class LivePriceListModel(
  @SerializedName("Ids") var list: ArrayList<Int>
)