package ir.tdaapp.tooka.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.CompareModel
import ir.tdaapp.tooka.models.CompareOHLCVModel
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.convertResponse
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import retrofit2.Call

class CompareViewModel: ViewModel() {

  private val _compareData = MutableLiveData<CompareModel>()
  val compareData: LiveData<CompareModel>
    get() = _compareData

  private val _chartData = MutableLiveData<CompareOHLCVModel>()
  val chartData: LiveData<CompareOHLCVModel>
    get() = _chartData

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  private val hubConnection = SignalR.hubConnection

  fun getAllCoins() {
    hubConnection.apply {
      if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
        send("GetComparableCoins")
      }

      on("ComparableCoins", {
        val response = convertResponse<List<Coin>>(it)
        _coinsList.postValue(response.result!!)

        with(response.result[0].id) {
          getData(this, this)
        }

      }, String::class.java)
    }
  }

  fun getData(firstCoinId: Int, secondCoinId: Int) {

    hubConnection.apply {
      if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
        send("GetComparedCoins", firstCoinId, secondCoinId)
      }

      on("CoinsToCompare", {
        val response = convertResponse<CompareModel>(it)
        _compareData.postValue(response.result!!)

      }, String::class.java)
    }
  }

  fun getChartData(firstCoinId: Int, secondCoinId: Int, timeFrameId: Int) {
    hubConnection.apply {
      if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
        send("GetCompareOhlcv", firstCoinId, secondCoinId, timeFrameId)
      }

      on("CompareOhlcv", {
        val response = convertResponse<CompareOHLCVModel>(it)
        _chartData.postValue(response.result!!)

      }, String::class.java)
    }
  }
}