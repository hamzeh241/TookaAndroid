package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.*
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import ir.tdaapp.tooka.util.detectError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class CoinDetailsViewModel(appClass: Application): AndroidViewModel(appClass) {

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

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private val hubConnection = SignalR.hubConnection

  private val retrofitClient: RetrofitClient by appClass.inject()

  suspend fun getData(id: Int) {
    /*

    retrofitClient.service.getSpecificCoin(id).enqueue(object: Callback<CoinDetailsModel> {
      override fun onResponse(call: Call<CoinDetailsModel>, response: Response<CoinDetailsModel>) {
        if (response.isSuccessful)
          _details.postValue(response.body())
      }

      override fun onFailure(call: Call<CoinDetailsModel>, t: Throwable) {
      }

    })

    retrofitClient.service.getRelatedNews(id, 3).enqueue(object: Callback<List<News>> {
      override fun onResponse(call: Call<List<News>>, response: Response<List<News>>) {
        if (response.isSuccessful)
          _relatedNews.postValue(response.body())
      }

      override fun onFailure(call: Call<List<News>>, t: Throwable) {
      }

    })

    retrofitClient.service.getRandomCoins(id).enqueue(object: Callback<List<Coin>> {
      override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
        if (response.isSuccessful)
          _otherCoins.postValue(response.body())
      }

      override fun onFailure(call: Call<List<Coin>>, t: Throwable) {
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
          hubConnection.send("GetCoinDetails", id)
        }
        withContext(Dispatchers.IO) {
          hubConnection.send("GetRelatedNews", id, 3)
          hubConnection.send("GetRandomCoins", id)
        }
      }
    } else _error.postValue(NetworkErrors.NETWORK_ERROR)

    hubConnection.on("CoinDetails", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<CoinDetailsModel>?>() {}.type
          val response: ResponseModel<CoinDetailsModel> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          if (response.status)
            _details.postValue(response.result)
          else {
            _error.postValue(detectError(response as ResponseModel<Any>))
          }
        }
      }
    }, String::class.java)
    hubConnection.on("RelatedNews", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<List<News>>?>() {}.type
          val response: ResponseModel<List<News>> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          if (response.status)
            _relatedNews.postValue(response.result)
          else {
            _error.postValue(detectError(response as ResponseModel<Any>))
          }
        }
      }
    }, String::class.java)
    hubConnection.on("RandomCoins", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<List<Coin>>?>() {}.type
          var response: ResponseModel<List<Coin>> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          if (response.status)
            _otherCoins.postValue(response.result)
          else {
            _error.postValue(detectError(response as ResponseModel<Any>))
          }
        }
      }
    }, String::class.java)
  }

  fun getChartData(coinId: Int, timeFrameId: Int) {
    /*

    retrofitClient.service.getOHLCV(coinId, timeFrameId).enqueue(object: Callback<List<List<Any>>> {
      override fun onResponse(call: Call<List<List<Any>>>, response: Response<List<List<Any>>>) {
        if (response.isSuccessful)
          _chartData.postValue(response.body()!!)
      }

      override fun onFailure(call: Call<List<List<Any>>>, t: Throwable) {
        var a = 1
        a++
      }
    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch {
        hubConnection.send("GetOhlcv", coinId, timeFrameId)
      }
    }
    hubConnection.on("Ohlcv", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<List<List<Any>>>?>() {}.type
          val response: ResponseModel<List<List<Any>>> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          if (response.status)
            _chartData.postValue(response.result)
          else {
            _error.postValue(detectError(response as ResponseModel<Any>))
          }
        }
      }
    }, String::class.java)
  }
}