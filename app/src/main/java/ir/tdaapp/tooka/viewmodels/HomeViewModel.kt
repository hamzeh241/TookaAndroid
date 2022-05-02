package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val api: ApiService): ViewModel() {

  private val _breakingNewsList = MutableLiveData<List<News>>()
  val breakingNewsList: LiveData<List<News>>
    get() = _breakingNewsList

  private val _topCoinsList = MutableLiveData<List<Coin>>()
  val topCoinsList: LiveData<List<Coin>>
    get() = _topCoinsList

  private val _gainersLosersList = MutableLiveData<List<Coin>>()
  val gainersLosersList: LiveData<List<Coin>>
    get() = _gainersLosersList

  private val _watchList = MutableLiveData<List<Coin>>()
  val watchList: LiveData<List<Coin>>
    get() = _watchList

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private var data: HomeContentResponse? = null

  init {
    viewModelScope.launch {
      getData()
    }
  }

  suspend fun getData() {
    try {
      if (data == null) {
        val data = api.homeData()

        if (data.isSuccessful) {
          this.data = data.body()?.result!!
          viewModelScope.launch(Dispatchers.IO) {
            _topCoinsList.postValue(data.body()!!.result.topCoins)
            delay(200)
            _breakingNewsList.postValue(data.body()!!.result.breakingNews)
            delay(200)
            _gainersLosersList.postValue(data.body()!!.result.gainersLosers)
            delay(200)
            _watchList.postValue(data.body()!!.result.watchlist)
          }
        } else {
          _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}