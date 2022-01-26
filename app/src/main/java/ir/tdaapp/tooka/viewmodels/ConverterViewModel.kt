package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.ConvertModel
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import retrofit2.Call

class ConverterViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val retrofit: RetrofitClient by appClass.inject()

  private var _coinList = MutableLiveData<List<Coin>>()
  val coinList: LiveData<List<Coin>>
    get() = _coinList

  private var _data = MutableLiveData<ConvertModel>()
  val data: LiveData<ConvertModel>
    get() = _data

  private val hubConnection = SignalR.hubConnection

  private lateinit var callList: Call<List<Coin>>
  private lateinit var call: Call<ConvertModel>

  fun getCoinList() {
    /*

    callList = retrofit.service.getCoinsToConvert()
    callList.enqueue(object: Callback<List<Coin>> {
      override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
        if (response.isSuccessful){
          _coinList.postValue(response.body())
          getConvertData(response.body()!![0].id, response.body()!![0].id)
        }
      }

      override fun onFailure(call: Call<List<Coin>>, t: Throwable) {
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("GetAllCoinsToConvert")
      }
    }
    hubConnection.on("AllCoinsConvert", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<List<Coin>>?>() {}.type
          val response: ResponseModel<List<Coin>> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          _coinList.postValue(response.result)
          getConvertData(response.result[0].id, response.result[0].id)
        }
      }

    }, String::class.java)
  }

  fun getConvertData(first: Int, second: Int) {
    /*

    call = retrofit.service.getConvertData(first,second)
    call.enqueue(object: Callback<ConvertModel> {
      override fun onResponse(call: Call<ConvertModel>, response: Response<ConvertModel>) {
        if (response.isSuccessful)
          _data.postValue(response.body())
      }

      override fun onFailure(call: Call<ConvertModel>, t: Throwable) {
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      GlobalScope.launch(Dispatchers.IO) {
        hubConnection.send("GetConvertCoins", first, second)
      }
    }
    hubConnection.on("CoinConvert", object: Action1<String> {
      override fun invoke(param1: String?) {
        GlobalScope.launch(Dispatchers.IO) {
          val collectionType = object: TypeToken<ResponseModel<ConvertModel>?>() {}.type
          var response: ResponseModel<ConvertModel> =
            GsonInstance.getInstance().fromJson(param1, collectionType)
          _data.postValue(response.result)
        }
      }

    }, String::class.java)
  }

  fun cancel() {
//    callList.cancel()
  }
}