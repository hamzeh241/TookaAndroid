package ir.tdaapp.tooka.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.*
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.*

class HomeViewModel(val applicationClass: Application): AndroidViewModel(applicationClass) {

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

  private val _livePrice = MutableLiveData<LivePriceListResponse>()
  val livePrice: LiveData<LivePriceListResponse>
    get() = _livePrice

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private var news = arrayListOf<News>()
  private var topCoins = arrayListOf<Coin>()
  private var gainersLosers = arrayListOf<Coin>()
  private var watchlist = arrayListOf<Coin>()

  var hubConnection: HubConnection = SignalR.hubConnection

  @DelicateCoroutinesApi
  suspend fun getData() {
    /*

    val retrofit: RetrofitClient by applicationClass.inject()

    retrofit.service.getData().enqueue(object: Callback<HomeContentResponse> {
      override fun onResponse(
        call: Call<HomeContentResponse>,
        response: Response<HomeContentResponse>
      ) {
        if (response.isSuccessful) {
          _breakingNewsList.postValue(response.body()!!.breakingNews)
          _topCoinsList.postValue(response.body()!!.topCoins)
          _gainersLosersList.postValue(response.body()!!.gainersLosers)
          _watchList.postValue(response.body()!!.watchlist)
        }
      }

      override fun onFailure(call: Call<HomeContentResponse>, t: Throwable) {

      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        if (topCoins.isNullOrEmpty())
          hubConnection.send("GetTopCoins")

        if (news.isNullOrEmpty())
          hubConnection.send("GetHomeNews")

        if (gainersLosers.isNullOrEmpty())
          hubConnection.send("GetGainersLosersCoins")

        if (watchlist.isNullOrEmpty())
          hubConnection.send("GetWatchlistCoins")
      }
    } else _error.postValue(NetworkErrors.NETWORK_ERROR)

    hubConnection.on("TopCoins", object: Action1<String> {
      override fun invoke(param1: String?) {
        val response = convertResponse<List<Coin>>(param1!!)

        topCoins = response.result as ArrayList<Coin>
        if (response.status)
          _topCoinsList.postValue(response.result)
        else {
          _error.postValue(detectError(response as ResponseModel<Any>))
        }
      }
    }, String::class.java)
    hubConnection.on("HomeNews", object: Action1<String> {
      override fun invoke(param1: String?) {
        val response = convertResponse<List<News>>(param1!!)

        news = response.result as ArrayList<News>
        if (response.status)
          _breakingNewsList.postValue(response.result)
        else {
          _error.postValue(detectError(response as ResponseModel<Any>))
        }
      }
    }, String::class.java)
    hubConnection.on("GainersLosers", object: Action1<String> {
      override fun invoke(param1: String?) {
        val response = convertResponse<List<Coin>>(param1!!)

        gainersLosers = response.result as ArrayList<Coin>
        if (response.status)
          _gainersLosersList.postValue(response.result)
        else {
          _error.postValue(detectError(response as ResponseModel<Any>))
        }
      }
    }, String::class.java)
    hubConnection.on("WatchlistCoins", object: Action1<String> {
      override fun invoke(param1: String?) {
        val response = convertResponse<List<Coin>>(param1!!)

        watchlist = response.result as ArrayList<Coin>
        if (response.status)
          _watchList.postValue(response.result)
        else {
          _error.postValue(detectError(response as ResponseModel<Any>))
        }
      }
    }, String::class.java)
  }

  fun subscribeToLivePrice() {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("SubscribeToLivePrice")
      }
    }

    hubConnection.on("LivePrice", { param1 ->

      val response = convertResponse<LivePriceListResponse>(param1!!)
      if (response.status)
        _livePrice.postValue(response.result)
      else {
        _error.postValue(detectError(response as ResponseModel<Any>))
      }

    }, String::class.java)
  }
}