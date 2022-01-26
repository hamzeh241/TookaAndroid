package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.CompareModel
import ir.tdaapp.tooka.models.CompareOHLCVModel
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import retrofit2.Call

class CompareViewModel(val applicationClass: Application): AndroidViewModel(applicationClass) {

  private val _compareData = MutableLiveData<CompareModel>()
  val compareData: LiveData<CompareModel>
    get() = _compareData

  private val _chartData = MutableLiveData<CompareOHLCVModel>()
  val chartData: LiveData<CompareOHLCVModel>
    get() = _chartData

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  val retrofit: RetrofitClient by applicationClass.inject()
  private val hubConnection = SignalR.hubConnection

  lateinit var coinCall: Call<List<Coin>>
  lateinit var compareCall: Call<CompareModel>
  lateinit var chartCall: Call<CompareOHLCVModel>

  fun getAllCoins() {
    /*
    coinCall = retrofit.service.getAllCoins()
    coinCall.enqueue(object: Callback<List<Coin>> {
        override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
          if (response.isSuccessful)
            _coinsList.postValue(response.body())
        }

        override fun onFailure(call: Call<List<Coin>>, t: Throwable) {
        }
      })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("GetAllCoins")
      }
    }
    hubConnection.on("AllCoins", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<List<Coin>>?>() {}.type
          var response: ResponseModel<List<Coin>> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          _coinsList.postValue(response.result)
        }
      }

    }, String::class.java)
  }

  fun getData(firstCoinId: Int, secondCoinId: Int) {
    /*

    compareCall = retrofit.service.getCompareData(firstCoinId, secondCoinId)
    compareCall.enqueue(object: Callback<CompareModel> {
      override fun onResponse(call: Call<CompareModel>, response: Response<CompareModel>) {
        if (response.isSuccessful)
          _compareData.postValue(response.body())
      }

      override fun onFailure(call: Call<CompareModel>, t: Throwable) {
        var a = 0
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("GetComparedCoins", firstCoinId, secondCoinId)
      }
    }
    hubConnection.on("CoinsToCompare", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<CompareModel>?>() {}.type
          var response: ResponseModel<CompareModel> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          _compareData.postValue(response.result)
        }
      }
    }, String::class.java)
  }

  fun getChartData(firstCoinId: Int, secondCoinId: Int, timeFrameId: Int) {
    /*

    chartCall = retrofit.service.getCompareOHLCV(firstCoinId, secondCoinId, timeFrameId)
    chartCall.enqueue(object: Callback<CompareOHLCVModel> {
      override fun onResponse(
        call: Call<CompareOHLCVModel>,
        response: Response<CompareOHLCVModel>
      ) {
        if (response.isSuccessful)
          _chartData.postValue(response.body())
      }

      override fun onFailure(call: Call<CompareOHLCVModel>, t: Throwable) {
        var a = 0
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("GetCompareOhlcv", firstCoinId, secondCoinId, timeFrameId)
      }
    }
    hubConnection.on("CompareOhlcv", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<CompareOHLCVModel>?>() {}.type
          val response: ResponseModel<CompareOHLCVModel> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          _chartData.postValue(response.result)
        }
      }
    }, String::class.java)
  }

  fun cancel() {
//    chartCall.cancel()
//    coinCall.cancel()
//    compareCall.cancel()
  }
}