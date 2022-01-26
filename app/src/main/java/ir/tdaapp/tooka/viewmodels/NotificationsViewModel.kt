package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.Notification
import ir.tdaapp.tooka.util.api.RetrofitClient
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsViewModel(private val appClass: Application): AndroidViewModel(appClass) {

  private val _result = MutableLiveData<List<Notification>>()
  val result: LiveData<List<Notification>>
    get() = _result

  private val retrofit: RetrofitClient by appClass.inject()

  lateinit var call: Call<List<Notification>>

  fun getData(apiKey: String, page: Int) {
    call = retrofit.service.getNotifications(apiKey, page)
    call.enqueue(object: Callback<List<Notification>> {
      override fun onResponse(
        call: Call<List<Notification>>,
        response: Response<List<Notification>>
      ) {
        if (response.isSuccessful) {
          _result.postValue(response.body())
        }
      }

      override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
      }
    })
  }

  fun cancel() = call.cancel()
}