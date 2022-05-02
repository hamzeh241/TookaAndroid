package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ir.tdaapp.tooka.models.dataclasses.AutoWalletModel
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.Platform
import ir.tdaapp.tooka.models.dataclasses.ResponseModel
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException

class AutomaticBottomSheetViewModel(private val api: ApiService): ViewModel() {

  private val _coins = MutableLiveData<List<Coin>>()
  val coins: LiveData<List<Coin>>
    get() = _coins

  private val _platforms = MutableLiveData<List<Platform>>()
  val platforms: LiveData<List<Platform>>
    get() = _platforms

  private val _postResult = MutableLiveData<Boolean>()
  val postResult: LiveData<Boolean>
    get() = _postResult

  private val _error = MutableLiveData<PortfolioErrors>()
  val error: LiveData<PortfolioErrors>
    get() = _error

  enum class PortfolioErrors{
    NETWORK_ERROR,
    INVALID_ARGS,
    NO_ARGS,
    NOT_FOUND,
    DATABASE_ERROR,
    SERVER_ERROR,
    UNKNOWN_ERROR
  }

  suspend fun getData() {
    try {
      val response = api.autoWalletCoins()
      if (response.isSuccessful)
        _coins.postValue(response.body()?.result!!)
      else {
        _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(PortfolioErrors.NETWORK_ERROR)
      else _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getPlatforms(coinId: Int) {
    try {
      val response = api.platformsByCoin(coinId)
      if (response.isSuccessful)
        _platforms.postValue(response.body()?.result!!)
      else {
        if (response.code().equals(400))
          _error.postValue(PortfolioErrors.INVALID_ARGS)
        else
          _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(PortfolioErrors.NETWORK_ERROR)
      else _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun addPortfolio(model: AutoWalletModel) {
    try {
      val response = api.addAutoPortfolio(model)
      if (response.isSuccessful)
        _postResult.postValue(response.body()?.result!!)
      else {
        val error = Gson().fromJson(
          response.errorBody()?.string(),
          ResponseModel
          ::class.java
        )

        when(error.code){
          -1 -> _error.postValue(PortfolioErrors.NO_ARGS)
          -2 -> _error.postValue(PortfolioErrors.NOT_FOUND)
          -3 -> _error.postValue(PortfolioErrors.INVALID_ARGS)
          -4 -> _error.postValue(PortfolioErrors.DATABASE_ERROR)
          -5 -> _error.postValue(PortfolioErrors.SERVER_ERROR)
          else -> _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(PortfolioErrors.NETWORK_ERROR)
      else _error.postValue(PortfolioErrors.UNKNOWN_ERROR)
    }
  }
}