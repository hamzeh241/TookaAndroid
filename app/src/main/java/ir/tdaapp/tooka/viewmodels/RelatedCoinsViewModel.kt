package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException

class RelatedCoinsViewModel(private val api: ApiService): ViewModel() {

  private val _coins = MutableLiveData<List<Coin>>()
  val coins: LiveData<List<Coin>>
    get() = _coins

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(id: Int, isRefreshing: Boolean = false) {
    if (isRefreshing) {
      callApi(id)
      return
    }
    if (coins.value != null) {
      _coins.postValue(coins.value)
    } else callApi(id)
  }

  private suspend fun callApi(id: Int) {
    try {
      val response = api.randomCoins(id)
      if (response.isSuccessful) {
        _coins.postValue(response.body()?.result!!)
      } else {
        when (response.code()) {
          400 ->
            _error.postValue(NetworkErrors.CLIENT_ERROR)
          500 ->
            _error.postValue(NetworkErrors.SERVER_ERROR)
          else ->
            _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}