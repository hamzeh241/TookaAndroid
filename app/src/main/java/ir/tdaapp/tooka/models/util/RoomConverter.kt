package ir.tdaapp.tooka.models.util

import androidx.room.TypeConverter
import com.google.gson.Gson

class RoomConverter {

  companion object {
    const val TAG = "RoomConverter"
  }

  @TypeConverter
  fun listToJson(value: List<Double>?) = Gson().toJson(value)

  @TypeConverter
  fun jsonToList(value: String) = try {
    Gson().fromJson(value, ArrayList::class.java).toList() as ArrayList<Double>
  } catch (e:Exception){
    ArrayList()
  }
}