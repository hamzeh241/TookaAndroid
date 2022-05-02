package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnectionState
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.convertResponse
import ir.tdaapp.tooka.util.signalr.SignalR
import org.koin.android.ext.android.inject

class NewsDetailsViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _newsDetails = MutableLiveData<NewsDetailsModel>()
  val newsDetails: LiveData<NewsDetailsModel>
    get() = _newsDetails

  private val retrofitClient: RetrofitClient by appClass.inject()
  private val hubConnection = SignalR.hubConnection

  fun getData(id: Int) = hubConnection.apply {
    if (connectionState == HubConnectionState.CONNECTED)
      send("GetNewsDetails", id)

    on("NewsDetails", {
      val response = convertResponse<NewsDetailsModel>(it)
      if (response.status)
        _newsDetails.postValue(response.result!!)
    }, String::class.java)
  }
}