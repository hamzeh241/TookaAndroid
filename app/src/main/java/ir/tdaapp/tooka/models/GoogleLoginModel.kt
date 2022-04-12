package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class GoogleLoginModel(
  @SerializedName("name") var name: String?,
  @SerializedName("family_name") var familyName: String?,
  @SerializedName("profileImage") var profileImage: String?,
  @SerializedName("email") var email: String
)