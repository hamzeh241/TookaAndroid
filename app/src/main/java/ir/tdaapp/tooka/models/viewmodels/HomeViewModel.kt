package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.HomeContentResponse
import ir.tdaapp.tooka.models.repositories.HomeRepository
import ir.tdaapp.tooka.models.util.NetworkErrors

class HomeViewModel(private val repository: HomeRepository): ViewModel() {

  private val _data = MutableLiveData<HomeContentResponse>()
  val data: LiveData<HomeContentResponse>
    get() = _data

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(userId: Int, refresh: Boolean = false) {
    if (repository.isEmpty()) {
      repository.getData(userId).collect {
        if (it.status) {
          _data.postValue(it.result!!)
          repository.addToDatabase(it.result)
        } else _error.postValue(it.errorType)
      }
    } else {
      if (!refresh)
        repository.getLocalData().collect {
          _data.postValue(it)
          callData(userId)
        }
      else
        callData(userId)
    }
  }

  private suspend fun callData(userId: Int) {
    repository.getData(userId).collect {
      if (it.status) {
        _data.postValue(it.result!!)
        repository.updateDatabase(it.result)
      } else _error.postValue(it.errorType)
    }
  }

}
