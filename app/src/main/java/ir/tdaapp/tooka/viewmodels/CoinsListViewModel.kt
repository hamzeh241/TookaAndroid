package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException
import java.lang.Exception

class CoinsListViewModel(private val api: ApiService): ViewModel() {

  private val _list = MutableLiveData<List<Coin>>()
  val list: LiveData<List<Coin>>
    get() = _list

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData() {
    try {
      val result = api.allCoins(false, sortOptions = 2)
      if (result.isSuccessful) {
        _list.postValue(result.body()?.result!!)
      } else {
        when(result.code()){
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}