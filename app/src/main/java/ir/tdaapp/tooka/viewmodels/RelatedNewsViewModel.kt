package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.api.RetrofitClient
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelatedNewsViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _news = MutableLiveData<List<News>>()
  val news: LiveData<List<News>>
    get() = _news

  private val retrofitClient:RetrofitClient by appClass.inject()

  fun getData(coinId: Int) {

    retrofitClient.service.getRelatedNews(coinId,0).enqueue(object: Callback<List<News>> {
      override fun onResponse(call: Call<List<News>>, response: Response<List<News>>) {
        if (response.isSuccessful)
          _news.postValue(response.body())
      }

      override fun onFailure(call: Call<List<News>>, t: Throwable) {
      }

    })

  }

}