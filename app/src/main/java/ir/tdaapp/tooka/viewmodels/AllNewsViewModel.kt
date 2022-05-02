package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.News
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.network.ApiService
import kotlinx.coroutines.launch

class AllNewsViewModel(private val api: ApiService): ViewModel() {

  companion object {
    const val TAG = "AllNewsViewModel"
  }

  init {
    viewModelScope.launch {
      getData()
    }
  }

  private val _news = MutableLiveData<List<News>>()
  val news: LiveData<List<News>>
    get() = _news

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(page: Int = 0) {
    val response = api.allNews(page)
    if (response.isSuccessful) {
      _news.postValue(response.body()?.result!!)
    } else {
      _error.postValue(NetworkErrors.SERVER_ERROR)
    }
  }
}