package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.NewsDetailsModel
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.signalr.SignalR
import org.koin.android.ext.android.inject

class NewsDetailsViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _newsDetails = MutableLiveData<NewsDetailsModel>()
  val newsDetails: LiveData<NewsDetailsModel>
    get() = _newsDetails

  private val retrofitClient: RetrofitClient by appClass.inject()
  private val hubConnection = SignalR.hubConnection

  fun getData(id: Int) {
    /*

    retrofitClient.service.getSpecificNews(id).enqueue(object:Callback<NewsDetailsModel> {
      override fun onResponse(call: Call<NewsDetailsModel>, response: Response<NewsDetailsModel>) {
        if (response.isSuccessful){
          _newsDetails.postValue(response.body())
        }
      }

      override fun onFailure(call: Call<NewsDetailsModel>, t: Throwable) {
      }

    })
     */

  }
}