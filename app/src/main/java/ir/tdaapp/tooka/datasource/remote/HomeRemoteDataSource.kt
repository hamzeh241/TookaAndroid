package ir.tdaapp.tooka.datasource.remote

import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.network.callApi

class HomeRemoteDataSource(private val api: ApiService) {

  companion object {
    const val TAG = "HomeRemoteDataSource"
  }

  suspend fun data() =
    callApi {
      api.homeData()
    }
}