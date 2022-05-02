package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class ResponseModel<T>(
  @SerializedName("code") var code: Int,
  @SerializedName("description") var description: String,
  @SerializedName("result") var result: T,
  @SerializedName("status") var status: Boolean
)