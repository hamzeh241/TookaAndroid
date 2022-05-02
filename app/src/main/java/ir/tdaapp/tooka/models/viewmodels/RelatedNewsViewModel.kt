package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.network.ApiService
import java.io.IOException

class RelatedNewsViewModel(private val api: ApiService): ViewModel() {

  private val _news = MutableLiveData<List<News>>()
  val news: LiveData<List<News>>
    get() = _news

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData(coinId: Int, isRefreshing: Boolean = false) {
    if (isRefreshing) {
      callApi(coinId)
      return
    }
    if (news.value != null) {
      _news.postValue(news.value)
    } else callApi(coinId)
  }

  private suspend fun callApi(coinId: Int) {
    try {

      val response = api.relatedNews(coinId)
      if (response.isSuccessful)
        _news.postValue(response.body()?.result!!)
      else {
        when (response.code()) {
          400 ->
            _error.postValue(NetworkErrors.CLIENT_ERROR)
          500 ->
            _error.postValue(NetworkErrors.SERVER_ERROR)
          else ->
            _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        }
      }
    }catch (e:Exception){
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

}