package ir.tdaapp.tooka.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.tdaapp.tooka.models.dao.HomeDao
import ir.tdaapp.tooka.models.dataclasses.GainersLosers
import ir.tdaapp.tooka.models.dataclasses.HomeNews
import ir.tdaapp.tooka.models.dataclasses.TopCoin
import ir.tdaapp.tooka.models.dataclasses.WatchlistCoin
import ir.tdaapp.tooka.models.util.RoomConverter

@Database(
  entities = arrayOf(
    TopCoin::class,
    HomeNews::class,
    GainersLosers::class,
    WatchlistCoin::class
  ), version = 1
)
@TypeConverters(RoomConverter::class)
abstract class TookaDatabase: RoomDatabase() {

  abstract fun homeDao(): HomeDao

  companion object {
    const val TAG = "TookaDatabase"

    @Volatile
    private var INSTANCE: TookaDatabase? = null

    fun instance(context: Context): TookaDatabase = INSTANCE ?: synchronized(this) {
      val instance = Room.databaseBuilder(
        context.applicationContext,
        TookaDatabase::class.java,
        "word_database"
      ).build()
      INSTANCE = instance
      // return instance
      instance
    }
  }
}
