package ir.tdaapp.tooka.models.dataclasses

import com.google.gson.annotations.SerializedName

data class LoginModel(
  @SerializedName("phone_number") var phoneNumber: String,
  @SerializedName("code") var code: Int
)