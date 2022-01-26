package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import org.koin.android.ext.android.inject

class MarketsViewModel(application: Application): AndroidViewModel(application) {

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  private val _sortList = MutableLiveData<List<SortModel>>()
  val sortList: LiveData<List<SortModel>>
    get() = _sortList

  private val _watchList = MutableLiveData<Boolean>()
  val watchList: LiveData<Boolean>
    get() = _watchList

  val retrofit: RetrofitClient by application.inject()
  val hubConnection = SignalR.hubConnection

  fun getData(ascend: Boolean, sortOption: Int) {
    /*

    retrofit.service.getAllCoins().enqueue(object: Callback<List<Coin>> {
      override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
        if (response.isSuccessful)
          _coinsList.postValue(response.body())
      }

      override fun onFailure(call: Call<List<Coin>>, t: Throwable) {
      }
    })
     */
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetAllCoins", 2,ascend, sortOption)
    }
    hubConnection.on("AllCoins", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<List<Coin>>?>() {}.type
        var response: ResponseModel<List<Coin>> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _coinsList.postValue(response.result)
      }

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