package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.network.ApiService
import java.io.IOException

class NotificationsViewModel(private val api: ApiService): ViewModel() {

  private val _result = MutableLiveData<List<Notification>>()
  val result: LiveData<List<Notification>>
    get() = _result

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(userId: Int) {
    try {
      val result = api.notifications(userId)
      if (result.isSuccessful)
        _result.postValue(result.body()?.result!!)
      else
        _error.postValue(NetworkErrors.SERVER_ERROR)
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}