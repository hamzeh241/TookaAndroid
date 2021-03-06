package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.tdaapp.tooka.models.dataclasses.PortfolioInfo
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.util.NetworkErrors
import java.io.IOException

class PortfolioViewModel(private val api: ApiService): ViewModel() {

  private val _capitals = MutableLiveData<PortfolioInfo>()
  val capitals: LiveData<PortfolioInfo>
    get() = _capitals

  private val _wallets = MutableLiveData<List<String>>()
  val wallets: LiveData<List<String>>
    get() = _wallets

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun getWallets(userId: Int) {
    try {
      val wallets = api.wallets(userId)
      if (wallets.isSuccessful)
        _wallets.postValue(wallets.body()!!.result!!)
      else {
        when (wallets.code()) {
          400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
          500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getAllBalances(userId: Int) {
    try {
      val balances = api.allBalances(userId)
      if (balances.isSuccessful)
        _capitals.postValue(balances.body()!!.result!!)
      else {
        when (balances.code()) {
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