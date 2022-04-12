package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.PortfolioInfo
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException

class PortfolioViewModel(private val api: ApiService): ViewModel() {

  private val _isPortfolio = MutableLiveData<Boolean>()
  val isPortfolio: LiveData<Boolean>
    get() = _isPortfolio

  private val _capitals = MutableLiveData<PortfolioInfo>()
  val capitals: LiveData<PortfolioInfo>
    get() = _capitals

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  fun isPortfolio(apiKey: String) {

  }

  suspend fun getAllBalances(userId: Int) {
    try {
      val balances = api.allBalances(userId)
      if (balances.isSuccessful)
        _capitals.postValue(balances.body()!!.result!!)
      else {
        when(balances.code()){
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          404 -> _error.postValue(NetworkErrors.NOT_FOUND_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {

      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }
}