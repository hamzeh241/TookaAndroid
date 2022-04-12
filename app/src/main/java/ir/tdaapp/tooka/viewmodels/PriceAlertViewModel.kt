package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ir.tdaapp.tooka.models.LivePriceListResponse
import ir.tdaapp.tooka.models.PriceAlertModel
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException
import java.lang.Exception

class PriceAlertViewModel(private val api: ApiService): ViewModel() {

  private val _price = MutableLiveData<LivePriceListResponse>()
  val price: LiveData<LivePriceListResponse>
    get() = _price

  private val _alertResult = MutableLiveData<Boolean>()
  val alertResult: LiveData<Boolean>
    get() = _alertResult

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private val _alertError = MutableLiveData<AlertStatus>()
  val alertError: LiveData<AlertStatus>
    get() = _alertError

  enum class AlertStatus {
    NOT_SAVED,
    INVALID_ARGUMENTS,
    NO_ARGUMENTS,
    UNKNOWN_ERROR,
    NETWORK_ERROR,
    SERVER_ERROR
  }

  suspend fun getCoinPrice(coinId: Int) {
    try {
      val price = api.coinPrice(coinId)
      if (price.isSuccessful) {
        _price.postValue(price.body()?.result!!)
      } else {
        when (price.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          404 -> _error.postValue(NetworkErrors.NOT_FOUND_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
          else -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun submitAlert(model: PriceAlertModel) {
    try {
      val result = api.submitAlert(model)
      if (result.isSuccessful) {
        _alertResult.postValue(result.body()?.result!!)
      } else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )

        when (result.code()) {
          400 -> {
            when (error.code) {
              -1 -> _alertError.postValue(AlertStatus.NO_ARGUMENTS)
              -2 -> _alertError.postValue(AlertStatus.INVALID_ARGUMENTS)
            }
          }
          500 -> {
            when (error.code) {
              -3 -> _alertError.postValue(AlertStatus.NOT_SAVED)
              -4 -> _alertError.postValue(AlertStatus.SERVER_ERROR)
            }
          }
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _alertError.postValue(AlertStatus.NETWORK_ERROR)
      else _alertError.postValue(AlertStatus.UNKNOWN_ERROR)
    }
  }
}