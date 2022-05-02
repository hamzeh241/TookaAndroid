package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.network.ApiService
import timber.log.Timber
import java.io.IOException

class SearchViewModel(private val api: ApiService): ViewModel() {

  private val _result = MutableLiveData<SearchResponse>()
  val result: LiveData<SearchResponse>
    get() = _result

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(input: String) {
    try {
      if (!input.isBlank()) {
        Timber.i("Is not blank")
        val response = api.search(input)
        if (response.isSuccessful) {
          Timber.i("Is successful")
          _result.postValue(response.body()?.result!!)
        } else {
          Timber.i("Is failed - ${response.code()}")
          when (response.code()) {
            400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
            500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
            else -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          }
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}