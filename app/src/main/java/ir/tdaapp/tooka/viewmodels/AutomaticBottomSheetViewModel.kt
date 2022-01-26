package ir.tdaapp.tooka.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutomaticBottomSheetViewModel(appClass: Application): AndroidViewModel(appClass) {

  private val _coins = MutableLiveData<List<Coin>>()
  val coins: LiveData<List<Coin>>
    get() = _coins

  private val _platforms = MutableLiveData<List<Platform>>()
  val platforms: LiveData<List<Platform>>
    get() = _platforms

  private val _balance = MutableLiveData<PostPortfolioResponse>()
  val balance: LiveData<PostPortfolioResponse>
    get() = _balance

  private val retrofit: RetrofitClient by appClass.inject()

  fun getData() {
    GlobalScope.launch(Dispatchers.IO) {
      retrofit.service.getCoinsForPortfolio().enqueue(object: Callback<List<Coin>> {
        override fun onResponse(call: Call<List<Coin>>, response: Response<List<Coin>>) {
          if (response.isSuccessful)
            _coins.postValue(response.body())
        }

        override fun onFailure(call: Call<List<Coin>>, t: Throwable) {

        }
      })
    }
  }

  fun getPlatforms(coinId: Int) {
    GlobalScope.launch(Dispatchers.IO) {
      retrofit.service.getPlatformsByCoin(coinId).enqueue(object: Callback<List<Platform>> {
        override fun onResponse(call: Call<List<Platform>>, response: Response<List<Platform>>) {
          if (response.isSuccessful)
            _platforms.postValue(response.body())
        }

        override fun onFailure(call: Call<List<Platform>>, t: Throwable) {
        }

      })
    }
  }

  fun addPortfolio(model: AutoWalletModel) {
    GlobalScope.launch(Dispatchers.IO) {
      retrofit.service.addPortfolio(model).enqueue(object: Callback<PostPortfolioResponse> {
        override fun onResponse(
          call: Call<PostPortfolioResponse>,
          response: Response<PostPortfolioResponse>
        ) {
          if (response.isSuccessful)
            _balance.postValue(response.body())
        }

        override fun onFailure(call: Call<PostPortfolioResponse>, t: Throwable) {
        }

      })
    }
  }
}