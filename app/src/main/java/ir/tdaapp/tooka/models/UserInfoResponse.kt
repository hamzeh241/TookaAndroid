package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
  @SerializedName("phone_number") val phone: String,
  @SerializedName("user_id") val id: Int
)
