package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.models.SliderNews
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException

class NewsViewModel(private val api: ApiService): ViewModel() {

  private val _sliderNews = MutableLiveData<List<SliderNews>>()
  val sliderNews: LiveData<List<SliderNews>>
    get() = _sliderNews

  private val _breakingNews = MutableLiveData<List<News>>()
  val breakingNews: LiveData<List<News>>
    get() = _breakingNews

  private val _allNews = MutableLiveData<List<News>>()
  val allNews: LiveData<List<News>>
    get() = _allNews

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getData() {
    try {
      val news = api.newsData()
      if (news.isSuccessful) {
        val result = news.body()!!.result
        _sliderNews.postValue(result.sliderNews)
        _breakingNews.postValue(result.breakingNews)
        _allNews.postValue(result.allNews)
      } else {
        _error.postValue(NetworkErrors.SERVER_ERROR)
      }

    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }

  }
}