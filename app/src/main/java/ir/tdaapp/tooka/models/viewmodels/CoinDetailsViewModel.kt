package ir.tdaapp.tooka.models.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class CoinDetailsViewModel(private val api: ApiService): ViewModel() {

  private val _details = MutableLiveData<CoinDetailsModel>()
  val details: LiveData<CoinDetailsModel>
    get() = _details

  private val _relatedNews = MutableLiveData<List<News>>()
  val relatedNews: LiveData<List<News>>
    get() = _relatedNews

  private val _otherCoins = MutableLiveData<List<Coin>>()
  val otherCoins: LiveData<List<Coin>>
    get() = _otherCoins

  private val _chartData = MutableLiveData<List<List<Any>>>()
  val chartData: LiveData<List<List<Any>>>
    get() = _chartData

  private val _timeframes = MutableLiveData<List<TimeFrameModel>>()
  val timeframes: LiveData<List<TimeFrameModel>>
    get() = _timeframes

  private val _livePrice = MutableLiveData<LivePriceListResponse>()
  val livePrice: LiveData<LivePriceListResponse>
    get() = _livePrice

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private val hubConnection = SignalR.hubConnection

  init {
    viewModelScope.launch {
      subscribeToCandleUpdate()
    }
  }

  fun getData(id: Int, count: Int) {
    viewModelScope.launch {
      getDetails(id)
      getTimeFrames(id)
      getRandomCoins(id, count)
    }
  }

  suspend fun subscribeToCandleUpdate() = withContext(Dispatchers.IO) {
    with(hubConnection) {
      send("SubscribeToCandleUpdate")
      on("CandleUpdate", {

      }, String::class.java)
    }
  }

  suspend fun getTimeFrames(coinId: Int) {
    try {
      val timeFrames = api.timeFrames(coinId)
      if (timeFrames.isSuccessful)
        _timeframes.postValue(timeFrames.body()?.result!!)
      else {
        when (timeFrames.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
          else -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getTimeFrames(firstId: Int, secondId: Int) {
    try {
      val timeFrames = api.mutualTimeFrames(firstId, secondId)
      if (timeFrames.isSuccessful)
        _timeframes.postValue(timeFrames.body()?.result!!)
      else {
        when (timeFrames.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
          else -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  private suspend fun getRandomCoins(id: Int, count: Int) {
    try {
      val randomCoins = api.randomCoins(id, count)
      if (randomCoins.isSuccessful) {
        _otherCoins.postValue(randomCoins.body()?.result!!)
      } else {
        when (randomCoins.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  private suspend fun getDetails(id: Int) {
    try {
      val details = api.coinDetails(id)
      if (details.isSuccessful) {
        _details.postValue(details.body()?.result!!)
        _relatedNews.postValue(details.body()?.result!!.relatedNews)
      } else {
        when (details.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          404 -> _error.postValue(NetworkErrors.NOT_FOUND_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getChartData(coinId: Int, timeFrameId: Int) {
    Timber.i("getChartData: ")
    try {
      val ohlcv = api.ohlcv(coinId, timeFrameId)
      if (ohlcv.isSuccessful) {
        _chartData.postValue(ohlcv.body()?.result!!)
      } else {
        when (ohlcv.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getToomanChartData(coinId: Int, timeFrameId: Int) {
    Timber.i("getToomanChartData: ")
    try {
      val ohlcv = api.irtOhlcv(coinId, timeFrameId)
      if (ohlcv.isSuccessful) {
        _chartData.postValue(ohlcv.body()?.result!!)
      } else {
        when (ohlcv.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      Log.i("TOOKALOG", "exception: ${e.toString()}")
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  override fun onCleared() {
    super.onCleared()
  }
}