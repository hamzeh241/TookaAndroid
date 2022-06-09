package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.HubConnection
import ir.tdaapp.tooka.models.dataclasses.CandleUpdateModel
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.util.GsonInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class MainActivityViewModel(private val hubConnection: HubConnection): ViewModel() {

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