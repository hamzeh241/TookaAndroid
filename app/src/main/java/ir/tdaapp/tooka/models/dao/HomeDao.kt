package ir.tdaapp.tooka.models.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.tdaapp.tooka.models.dataclasses.GainersLosers
import ir.tdaapp.tooka.models.dataclasses.HomeNews
import ir.tdaapp.tooka.models.dataclasses.TopCoin
import ir.tdaapp.tooka.models.dataclasses.WatchlistCoin
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

  @Query("SELECT COUNT(*) from top_coins")
  suspend fun topCount(): Int
  @Query("SELECT COUNT(*) from gainers_losers")
  suspend fun gainersCount(): Int
  @Query("SELECT COUNT(*) from home_news")
  suspend fun newsCount(): Int

  @Insert
  suspend fun addTopCoins(coins: List<TopCoin>)

  @Update
  suspend fun updateTopCoins(coins: List<TopCoin>)
  @Update
  suspend fun updateGainersLosers(coins: List<GainersLosers>)
  @Update
  suspend fun updateWatchlistCoins(coins: List<WatchlistCoin>)

  @Query("SELECT * from top_coins ORDER BY rank")
  fun topCoins(): Flow<List<TopCoin>>

  @Query("DELETE from top_coins")
  suspend fun clearTopCoins()

  @Insert
  suspend fun addBreakingNews(news: List<HomeNews>)

  @Query("SELECT * from home_news ORDER BY news_id DESC LIMIT 6")
  fun breakingNews(): Flow<List<HomeNews>>

  @Query("DELETE from home_news")
  suspend fun clearBreakingNews()

  @Insert
  suspend fun addGainersLosers(coins: List<GainersLosers>)

  @Query("SELECT * from gainers_losers ORDER BY id DESC")
  fun gainersLosers(): Flow<List<GainersLosers>>

  @Query("DELETE from gainers_losers")
  suspend fun clearGainersLosers()

  @Insert
  suspend fun addWatchlistCoins(coins: List<WatchlistCoin>)

  @Query("SELECT * from watchlist_coins ORDER BY id DESC")
  fun watchlistCoins(): Flow<List<WatchlistCoin>>

  @Query("DELETE from watchlist_coins")
  suspend fun clearWatchlistCoins()
}