package ir.tdaapp.tooka.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonInstance {

  var gson: Gson? = null

  fun getInstance(): Gson {
    if (gson == null) {
      val builder = GsonBuilder()
      builder.setPrettyPrinting()
      gson = builder.create()
      return gson!!
    } else return gson!!
  }
}