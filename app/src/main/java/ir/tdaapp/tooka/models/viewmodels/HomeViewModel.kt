package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.HomeContentResponse
import ir.tdaapp.tooka.models.repositories.HomeRepository
import ir.tdaapp.tooka.models.util.NetworkErrors
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val repositoy: HomeRepository): ViewModel() {

  private val _data = MutableLiveData<HomeContentResponse>()
  val data: LiveData<HomeContentResponse>
    get() = _data

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  init {
    viewModelScope.launch {
      getData()
    }
  }

  suspend fun getData() {
    if (repositoy.isEmpty()) {
      Timber.i("is empty")
      repositoy.getData().collect {
        if (it.status) {
          _data.value = it.result!!
          repositoy.addToDatabase(it.result)
        } else _error.postValue(it.errorType)
      }
    } else {
      Timber.i("is not empty")
      repositoy.getLocalData().collect {
        _data.value = it
      }

      repositoy.getData().collect {
        if (it.status) {
          _data.value = it.result!!
          repositoy.updateDatabase(it.result)
        } else _error.postValue(it.errorType)
      }
    }
  }

}
