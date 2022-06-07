package ir.tdaapp.tooka.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.tdaapp.tooka.models.dao.HomeDao
import ir.tdaapp.tooka.models.dao.MarketsDao
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.RoomConverter

@Database(
  entities = arrayOf(
    TopCoin::class,
    HomeNews::class,
    GainersLosers::class,
    WatchlistCoin::class,
    Coin::class
  ), version = 1
)
@TypeConverters(RoomConverter::class)
abstract class TookaDatabase: RoomDatabase() {

  abstract fun homeDao(): HomeDao
  abstract fun marketsDao(): MarketsDao

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
