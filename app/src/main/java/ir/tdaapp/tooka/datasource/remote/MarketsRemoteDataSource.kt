package ir.tdaapp.tooka.datasource.remote

import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.network.callApi

class MarketsRemoteDataSource(private val api: ApiService) {

  companion object {
    const val TAG = "MarketsRemoteDataSource"
  }

  suspend fun allCoins(
    ascend: Boolean,
    sortOptions: Int,
    userId: Int = 0
  ) = callApi {
    api.allCoins(ascend, userId, sortOptions)
  }

  suspend fun sortOptions() = callApi {
    api.sortOptions()
  }

  suspend fun addToWatchlist(userId: Int, coinId: Int) = callApi {
    api.addWatchlist(coinId, userId)
  }
}