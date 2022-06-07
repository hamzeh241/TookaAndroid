package ir.tdaapp.tooka.datasource.local

import ir.tdaapp.tooka.models.dao.MarketsDao
import ir.tdaapp.tooka.models.dataclasses.Coin

class MarketsLocalDataSource(private val dao: MarketsDao) {

  companion object {
    const val TAG = "MarketsLocalDataSource"
  }

  suspend fun count() = dao.count()

  fun coins() = dao.coins()

  suspend fun clearDatabase() = dao.clearCoins()

  suspend fun updateDatabase(coins: List<Coin>) = dao.updateCoins(coins)

  suspend fun addCoins(coins: List<Coin>) = dao.addCoins(coins)

}