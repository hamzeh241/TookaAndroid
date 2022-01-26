package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class LivePriceListModel(
  @SerializedName("Ids") var list: ArrayList<Int>
)
