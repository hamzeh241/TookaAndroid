package ir.tdaapp.tooka.models.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.tdaapp.tooka.models.dataclasses.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketsDao {

  @Query("SELECT COUNT(*) from coins")
  suspend fun count(): Int

  @Insert
  suspend fun addCoins(coins: List<Coin>)

  @Update
  suspend fun updateCoins(coins: List<Coin>)

  @Query("SELECT * from coins ORDER BY rank")
  fun coins(): Flow<List<Coin>>

  @Query("DELETE from coins")
  suspend fun clearCoins()

}