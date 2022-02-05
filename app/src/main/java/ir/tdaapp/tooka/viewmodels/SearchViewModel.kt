package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.SearchResponse
import ir.tdaapp.tooka.util.api.RetrofitClient
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(val appClass: Application): AndroidViewModel(appClass) {

  private val _result = MutableLiveData<SearchResponse>()
  val result: LiveData<SearchResponse>
    get() = _result

  val retrofit: RetrofitClient by appClass.inject()

  lateinit var call: Call<SearchResponse>

  fun getData(input: String) {
    call = retrofit.service.search(input)
    call.enqueue(object: Callback<SearchResponse> {
      override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
        if (response.isSuccessful) {
          _result.postValue(response.body())
        }
      }

      override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
      }
    })
  }

  fun cancel() {
    if (call != null)
      call.cancel()
  }
}