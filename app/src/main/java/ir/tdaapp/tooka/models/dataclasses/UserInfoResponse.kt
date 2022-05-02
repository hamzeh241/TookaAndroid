package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
  @SerializedName("phone_number") val phone: String,
  @SerializedName("user_id") val id: Int
)