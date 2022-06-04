package ir.tdaapp.tooka.models.repositories

import ir.tdaapp.tooka.datasource.local.HomeLocalDataSource
import ir.tdaapp.tooka.datasource.remote.HomeRemoteDataSource
import ir.tdaapp.tooka.models.dataclasses.HomeContentResponse
import ir.tdaapp.tooka.models.util.toCoin
import ir.tdaapp.tooka.models.util.toNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HomeRepository(
  private val local: HomeLocalDataSource,
  private val remote: HomeRemoteDataSource
) {

  companion object {
    const val TAG = "HomeRepository"
  }

  private fun getTopCoins() = local.topCoins()
    .map { it.map { it.toCoin() } }

  private fun getGainersLosersCoins() = local.gainersLosers()
    .map { it.map { it.toCoin() } }

  private fun getWatchlistCoins() = local.watchlistCoins()
    .map { it.map { it.toCoin() } }

  private fun getBreakingNews() = local.homeNews()
    .map { it.map { it.toNews() } }

  suspend fun getLocalData(): Flow<HomeContentResponse> =
    flowOf(
      HomeContentResponse(
        getTopCoins().firstOrNull() ?: emptyList(),
        getBreakingNews().firstOrNull() ?: emptyList(),
        getGainersLosersCoins().firstOrNull() ?: emptyList(),
        getWatchlistCoins().firstOrNull() ?: emptyList()
      )
    )

  suspend fun isEmpty() = local.count() <= 0

  suspend fun updateDatabase(response: HomeContentResponse) = local.updateDatabase(response)

  suspend fun clearDatabase() = local.clearDatabase()

  suspend fun addToDatabase(response: HomeContentResponse) = local.addToDatabase(response)

  suspend fun getData(userId:Int) = flowOf(remote.data(userId))
}