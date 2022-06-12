package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.News
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.util.NetworkErrors
import kotlinx.coroutines.launch
import java.io.IOException

class BreakingNewsViewModel(private val api: ApiService): ViewModel() {

  companion object {
    const val TAG = "BreakingNewsViewModel"
  }

  private val _news = MutableLiveData<List<News>>()
  val news: LiveData<List<News>>
    get() = _news

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  init {
    viewModelScope.launch {
      getBreakingNews()
    }
  }

  suspend fun getBreakingNews() {
    try {
      val news = api.breakingNews()
      if (news.isSuccessful) {
        _news.postValue(news.body()!!.result!!)
      } else _error.postValue(NetworkErrors.SERVER_ERROR)
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}