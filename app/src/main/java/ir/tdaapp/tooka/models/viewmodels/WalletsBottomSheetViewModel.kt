package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.tdaapp.tooka.models.network.ApiService
import ir.tdaapp.tooka.models.util.NetworkErrors
import java.io.IOException

class WalletsBottomSheetViewModel(private val api: ApiService) {

  companion object {
    const val TAG = "WalletsBottomSheetDialogViewModel"
  }

  private val _result = MutableLiveData<Boolean>()
  val result: LiveData<Boolean>
    get() = _result

  private val _wallets = MutableLiveData<List<String>>()
  val wallets: LiveData<List<String>>
    get() = _wallets

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  suspend fun deleteWallet(userId: Int, walletAddress: String) {
    try {
      val result = api.deleteWallet(userId, walletAddress)
      if (result.isSuccessful)
        _result.postValue(result.body()!!.result!!)
      else {
        when (result.code()) {
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
}