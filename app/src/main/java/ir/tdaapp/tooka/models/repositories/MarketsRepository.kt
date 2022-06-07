package ir.tdaapp.tooka.models.repositories

import ir.tdaapp.tooka.datasource.local.MarketsLocalDataSource
import ir.tdaapp.tooka.datasource.remote.MarketsRemoteDataSource
import ir.tdaapp.tooka.models.dataclasses.AddWatchlistResult
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.ResponseModel
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.util.WatchlistErrors

class MarketsRepository(
  private val local: MarketsLocalDataSource,
  private val remote: MarketsRemoteDataSource
) {

  companion object {
    const val TAG = "MarketsRepository"
  }

  suspend fun isEmpty() = local.count() <= 0

  fun getLocalData() = local.coins()

  suspend fun updateDatabase(coins: List<Coin>) = local.updateDatabase(coins)

  suspend fun clearDatabase() = local.clearDatabase()

  suspend fun addToDatabase(coins: List<Coin>) = local.addCoins(coins)

  suspend fun getData(
    ascend: Boolean,
    sortOptions: Int,
    userId: Int = 0
  ): ResponseModel<List<Coin>> {
    val result = remote.allCoins(ascend, sortOptions, userId)
    if (!result.status) {
      when (result.code) {
        -1 -> result.errorType = NetworkErrors.CLIENT_ERROR
        -2 -> result.errorType = NetworkErrors.SERVER_ERROR
      }
    }
    return result
  }

  suspend fun getSortOptions() = remote.sortOptions()

  suspend fun addWatchlist(userId: Int, coinId: Int): Pair<ResponseModel<AddWatchlistResult>, WatchlistErrors?> {
    val result = remote.addToWatchlist(userId, coinId)

    if (!result.status) {
      val pair = Pair(
        result, when (result.code) {
          -1 -> WatchlistErrors.INVALID_ARGS
          -2 -> WatchlistErrors.DATA_NOT_SAVED
          -3 -> WatchlistErrors.USER_NOT_FOUND
          -4 -> WatchlistErrors.COIN_NOT_FOUND
          -5 -> WatchlistErrors.SERVER_ERROR
          else -> WatchlistErrors.UNKNOWN_ERROR
        }
      )
      return pair
    }

    return Pair(result,null)
  }
}