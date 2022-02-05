package ir.tdaapp.tooka.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.LivePriceListResponse
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.convertResponse
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MarketsViewModel: ViewModel() {

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  private val _sortList = MutableLiveData<List<SortModel>>()
  val sortList: LiveData<List<SortModel>>
    get() = _sortList

  private val _watchList = MutableLiveData<Boolean>()
  val watchList: LiveData<Boolean>
    get() = _watchList

  private val _livePrice = MutableLiveData<LivePriceListResponse>()
  val livePrice: LiveData<LivePriceListResponse>
    get() = _livePrice

  //  val retrofit: RetrofitClient by application.inject()
  val hubConnection = SignalR.hubConnection

  var isSubscribed = false

  fun getData(ascend: Boolean, sortOption: Int) {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED)
      viewModelScope.launch(Dispatchers.IO) {
        //bug => should send userid instead of 2
        hubConnection.send("GetAllCoins", 2, ascend, sortOption)
      }

    hubConnection.on("AllCoins", { json ->
      val response = convertResponse<List<Coin>>(json!!)

      if (response.status) {
        _coinsList.postValue(response.result!!)
        if (!isSubscribed)
          subscribeLivePrice()
      }
    }, String::class.java)

  }

  fun subscribeLivePrice() {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED)
      viewModelScope.launch(Dispatchers.IO) {
        hubConnection.send("SubscribeToLivePrice")
      }

    hubConnection.on("GroupChange", { response ->
      isSubscribed = true
    }, String::class.java)

    hubConnection.on("LivePrice", { response ->
      val collectionType = object: TypeToken<LivePriceListResponse?>() {}.type
      val model: LivePriceListResponse =
        GsonInstance.getInstance().fromJson(response, collectionType)

      _livePrice.postValue(model)
    }, String::class.java)
  }

  fun getSortOptions() {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetSortOptions")
    }

    hubConnection.on("SortOptions", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<List<SortModel>>?>() {}.type
        var response: ResponseModel<List<SortModel>> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _sortList.postValue(response.result)
      }

    }, String::class.java)
  }

  fun addToWatchlist(userId: Int, coinId: Int) {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("AddToWatchlist", userId, coinId)
    }

    hubConnection.on("AddWatchlist", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<Boolean>?>() {}.type
        var response: ResponseModel<Boolean> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _watchList.postValue(response.result)
      }
    }, String::class.java)
  }

}