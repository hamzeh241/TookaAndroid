package ir.tdaapp.tooka.models


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("desc_en")
    val descEn: String,
    @SerializedName("desc_fa")
    val descFa: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("kind")
    val kind: Int,
    @SerializedName("title_en")
    val titleEn: String,
    @SerializedName("title_fa")
    val titleFa: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("is_public")
    var isPublic:Boolean,
    @SerializedName("create_date")
    var createDate:String
)