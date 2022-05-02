package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.network.ApiService
import java.io.IOException

class ManualBottomSheetViewModel(private val api: ApiService): ViewModel() {

  private val _coins = MutableLiveData<List<Coin>>()
  val coins: LiveData<List<Coin>>
    get() = _coins

  private val _postResult = MutableLiveData<Boolean>()
  val postResult: LiveData<Boolean>
    get() = _postResult

  private val _error = MutableLiveData<ManualPortfolioErrors>()
  val error: LiveData<ManualPortfolioErrors>
    get() = _error

  enum class ManualPortfolioErrors {
    NO_ARGS,
    INVALID_ARGS,
    NOT_SAVED,
    NETWORK_ERROR,
    SERVER_ERROR,
    UNKNOWN_ERROR
  }

  suspend fun getData() {
    try {
      val result = api.allCoins(false, sortOptions = 2)
      if (result.isSuccessful)
        _coins.postValue(result.body()?.result!!)
      else {
        when (result.code()) {
          400 -> _error.postValue(ManualPortfolioErrors.INVALID_ARGS)
          500 -> _error.postValue(ManualPortfolioErrors.SERVER_ERROR)
          else -> _error.postValue(ManualPortfolioErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(ManualPortfolioErrors.NETWORK_ERROR)
      else _error.postValue(ManualPortfolioErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun postData(model: ManualWalletModel) {
    try {
      val result = api.addManualPortfolio(model)
      if (result.isSuccessful)
        _postResult.postValue(result.body()?.result!!)
      else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when (error.code) {
          -1 -> _error.postValue(ManualPortfolioErrors.NO_ARGS)
          -2 -> _error.postValue(ManualPortfolioErrors.INVALID_ARGS)
          -3 -> _error.postValue(ManualPortfolioErrors.NOT_SAVED)
          -4 -> _error.postValue(ManualPortfolioErrors.SERVER_ERROR)
          else -> _error.postValue(ManualPortfolioErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(ManualPortfolioErrors.NETWORK_ERROR)
      else _error.postValue(ManualPortfolioErrors.UNKNOWN_ERROR)
    }
  }
}