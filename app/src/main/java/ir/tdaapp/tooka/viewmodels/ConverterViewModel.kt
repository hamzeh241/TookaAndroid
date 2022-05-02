package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.*
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.convertResponse
import ir.tdaapp.tooka.models.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConverterViewModel: ViewModel() {

  private var _coinList = MutableLiveData<List<Coin>>()
  val coinList: LiveData<List<Coin>>
    get() = _coinList

  private var _data = MutableLiveData<ConvertModel>()
  val data: LiveData<ConvertModel>
    get() = _data

  private val hubConnection = SignalR.hubConnection

  suspend fun getCoinList() = withContext(Dispatchers.IO) {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetAllCoinsToConvert")
    }
    hubConnection.on("AllCoinsConvert", {
      val response = convertResponse<List<Coin>>(it)
      _coinList.postValue(response.result!!)

      viewModelScope.launch {
        getConvertData(response.result[0].id, response.result[0].id)
      }
    }, String::class.java)
  }

  suspend fun getConvertData(first: Int, second: Int) = withContext(Dispatchers.IO) {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetConvertCoins", first, second)
    }
    hubConnection.on("CoinConvert", {
      val response = convertResponse<ConvertModel>(it)
      _data.postValue(response.result!!)

    }, String::class.java)
  }
}