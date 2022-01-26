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
import ir.tdaapp.tooka.util.signalr.SignalR

class NewsViewModel(val appClass: Application): AndroidViewModel(appClass) {

  private val _sliderNews = MutableLiveData<List<SliderNews>>()
  val sliderNews: LiveData<List<SliderNews>>
    get() = _sliderNews

  private val _breakingNews = MutableLiveData<List<News>>()
  val breakingNews: LiveData<List<News>>
    get() = _breakingNews

  private val _allNews = MutableLiveData<List<News>>()
  val allNews: LiveData<List<News>>
    get() = _allNews

  private val hubConnection = SignalR.hubConnection

  fun getData() {
    /*

    val retrofit: RetrofitClient by appClass.inject()

    retrofit.service.getNewsData().enqueue(object: Callback<NewsContentResponse> {
      override fun onResponse(
        call: Call<NewsContentResponse>,
        response: Response<NewsContentResponse>
      ) {
        if (response.isSuccessful) {
          _sliderNews.postValue(response.body()!!.sliderNews)
          _breakingNews.postValue(response.body()!!.breakingNews)
          _allNews.postValue(response.body()!!.allNews)
        }
      }

      override fun onFailure(call: Call<NewsContentResponse>, t: Throwable) {
        var a = 0
        a++
      }

    })
     */
    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
      hubConnection.send("GetPreviewAllNews")
      hubConnection.send("GetBreakingNews")
      hubConnection.send("GetSliderNews")
    }

    hubConnection.on("PreviewAllNews", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<List<News>>?>() {}.type
        var response: ResponseModel<List<News>> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _allNews.postValue(response.result)
      }
    }, String::class.java)
    hubConnection.on("BreakingNews", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<List<News>>?>() {}.type
        var response: ResponseModel<List<News>> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _breakingNews.postValue(response.result)
      }
    }, String::class.java)
    hubConnection.on("SliderNews", object: Action1<String> {
      override fun invoke(param1: String?) {
        val collectionType = object: TypeToken<ResponseModel<List<SliderNews>>?>() {}.type
        var response: ResponseModel<List<SliderNews>> =
          GsonInstance.getInstance().fromJson(param1, collectionType)
        _sliderNews.postValue(response.result)
      }
    }, String::class.java)

  }
}