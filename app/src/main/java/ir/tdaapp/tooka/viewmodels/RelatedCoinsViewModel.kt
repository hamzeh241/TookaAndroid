package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.util.api.RetrofitClient
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelatedCoinsViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _coins = MutableLiveData<List<Coin>>()
  val coins: LiveData<List<Coin>>
    get() = _coins

  private val retrofitClient: RetrofitClient by appClass.inject()

  fun getData(id:Int) {
    retrofitClient.service.getRandomCoins(id).enqueue(object: Callback<List<Coin>> {
      override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
        if (response.isSuccessful)
          _coins.postValue(response.body())
      }

      override fun onFailure(call: Call<List<Coin>>, t: Throwable) {
      }
    })
  }
}