package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.PortfolioInfo
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import org.koin.android.ext.android.inject

class PortfolioViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _isPortfolio = MutableLiveData<Boolean>()
  val isPortfolio: LiveData<Boolean>
    get() = _isPortfolio

  private val _capitals = MutableLiveData<PortfolioInfo>()
  val capitals: LiveData<PortfolioInfo>
    get() = _capitals

  private val retrofitClient: RetrofitClient by appClass.inject()
  private val hubConnection = SignalR.hubConnection

  fun isPortfolio(apiKey: String) {
    /*

    retrofitClient.service.isPortfolioActivated(apiKey).enqueue(object: Callback<Boolean> {
      override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
        if (response.isSuccessful)
          _isPortfolio.postValue(response.body())
        else if (response.code() == RetrofitClient.NOT_FOUND)
          _isPortfolio.postValue(false)
      }

      override fun onFailure(call: Call<Boolean>, t: Throwable) {
      }
    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("DoesHavePortfolio")
    }

    hubConnection.on("HavePortfolio", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<Boolean>?>() {}.type
        var response: ResponseModel<Boolean> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
      }
    }, String::class.java)
  }

  fun getAllBalances(apiKey: String) {
    /*

    retrofitClient.service.getBalances(apiKey).enqueue(object: Callback<PortfolioInfo> {
      override fun onResponse(
        call: Call<PortfolioInfo>,
        response: Response<PortfolioInfo>
      ) {
        if (response.isSuccessful) {
          _capitals.postValue(response.body())
        }
      }

      override fun onFailure(call: Call<PortfolioInfo>, t: Throwable) {
      }

    })
     */

    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetAllBalances",apiKey)
    }
    hubConnection.on("AllBalances", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<PortfolioInfo>?>() {}.type
        var response: ResponseModel<PortfolioInfo> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _capitals.postValue(response.result)
      }

    }, String::class.java)
  }
}