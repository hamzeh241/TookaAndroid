package ir.tdaapp.tooka.datasource.local

import ir.tdaapp.tooka.models.dao.HomeDao
import ir.tdaapp.tooka.models.dataclasses.HomeContentResponse
import ir.tdaapp.tooka.models.util.toGainersLosers
import ir.tdaapp.tooka.models.util.toHomeNews
import ir.tdaapp.tooka.models.util.toTopCoin
import ir.tdaapp.tooka.models.util.toWatchlist

class HomeLocalDataSource(private val dao: HomeDao) {

  companion object {
    const val TAG = "HomeLocalDataSource"
  }

  fun topCoins() = dao.topCoins()
  fun homeNews() = dao.breakingNews()
  fun gainersLosers() = dao.gainersLosers()
  fun watchlistCoins() = dao.watchlistCoins()

  suspend fun topCount() = dao.topCount()
  suspend fun newsCount() = dao.newsCount()
  suspend fun gainersCount() = dao.gainersCount()

  suspend fun clearDatabase() = dao.run {
    clearTopCoins()
    clearGainersLosers()
    clearWatchlistCoins()
    clearBreakingNews()
  }

  suspend fun updateDatabase(contentResponse: HomeContentResponse) = dao.run {
    updateTopCoins(contentResponse.topCoins.map { it.toTopCoin() })
    updateWatchlistCoins(contentResponse.watchlist.map { it.toWatchlist() })
    updateGainersLosers(contentResponse.gainersLosers.map { it.toGainersLosers() })
  }

  suspend fun addToDatabase(contentResponse: HomeContentResponse) = dao.run {
    addTopCoins(contentResponse.topCoins.map { it.toTopCoin() })
    addBreakingNews(contentResponse.breakingNews.map { it.toHomeNews() })
    addGainersLosers(contentResponse.gainersLosers.map { it.toGainersLosers() })
    addWatchlistCoins(contentResponse.watchlist.map { it.toWatchlist() })
  }
}