package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class AddWatchlistResult(
  @SerializedName("coinId")
  val coinId: Int,
  @SerializedName("status")
  val status: Boolean
)