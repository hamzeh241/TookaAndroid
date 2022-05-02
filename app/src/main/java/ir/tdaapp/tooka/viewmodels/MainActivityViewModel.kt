package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.GsonInstance
import ir.tdaapp.tooka.util.signalr.SignalR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class MainActivityViewModel: ViewModel() {

  private val hubConnection = SignalR.hubConnection

  init {
    subscribeToLivePrice()
  }

  fun subscribeToLivePrice() = hubConnection.apply {
    send("SubscribeToLivePrice")
    observeLivePrice()
  }

  fun subscribeToCandleUpdate() = hubConnection.apply {
    send("SubscribeToCandleUpdate")
    observeCandleUpdate()
  }

  fun unsubscribeFromCandleUpdate() = hubConnection.apply {
    send("UnsubscribeToCandleUpdate")
  }

  private fun observeCandleUpdate() = hubConnection.apply {
    on("CandleUpdate", {
      viewModelScope.launch(Dispatchers.IO) {
        val collectionType = object: TypeToken<List<CandleUpdateModel>?>() {}.type
        val response: List<CandleUpdateModel> =
          GsonInstance.getInstance().fromJson(it, collectionType)

        EventBus.getDefault().post(response)
      }
    }, String::class.java)
  }

  private fun observeLivePrice() = hubConnection.apply {
    on("LivePrice", { param1 ->

      viewModelScope.launch(Dispatchers.IO) {
        val collectionType = object: TypeToken<LivePriceListResponse?>() {}.type
        val response: LivePriceListResponse =
          GsonInstance.getInstance().fromJson(param1, collectionType)

        EventBus.getDefault().post(response)
      }
    }, String::class.java)
  }
}