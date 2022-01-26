package ir.tdaapp.tooka.models

import android.media.MediaDrm
import com.google.gson.annotations.SerializedName

data class Message(
  @SerializedName("status_code") var statusCode: Int,
  @SerializedName("message") var message: String,
  @SerializedName("result") var result: Boolean
)
