package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _error = MutableLiveData<String>()
  val error: LiveData<String>
    get() = _error

  fun sendError(message: String) {
    _error.postValue(message)
  }
}