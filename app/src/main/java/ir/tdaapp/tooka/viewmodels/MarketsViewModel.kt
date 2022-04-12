package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.ResponseModel
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.util.api.ApiService
import kotlinx.coroutines.launch
import java.io.IOException

class MarketsViewModel(private val api: ApiService): ViewModel() {

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  private val _sortList = MutableLiveData<List<SortModel>>()
  val sortList: LiveData<List<SortModel>>
    get() = _sortList

  private val _watchList = MutableLiveData<Boolean>()
  val watchList: LiveData<Boolean>
    get() = _watchList

  private val _error = MutableLiveData<NetworkErrors>()
  val error: LiveData<NetworkErrors>
    get() = _error

  private val _watchlistError = MutableLiveData<WatchlistErrors>()
  val watchlistError: LiveData<WatchlistErrors>
    get() = _watchlistError

  private var _selectedSort = MutableLiveData<SortModel>()
  val selectedSort: LiveData<SortModel>
    get() = _selectedSort

  val viewType = MutableLiveData<ViewType>()

  val lastScrollPosition = MutableLiveData<Int>()

  enum class ViewType {
    Linear,
    Grid
  }

  enum class WatchlistErrors {
    COIN_NOT_FOUND,
    USER_NOT_FOUND,
    DATA_NOT_SAVED,
    INVALID_ARGS,
    NETWORK_ERROR,
    SERVER_ERROR,
    UNKNOWN_ERROR
  }

  init {
    viewModelScope.launch {
      getSortOptions()
    }
    viewType.value = ViewType.Linear
  }

  fun setSelected(model: SortModel) = _selectedSort.postValue(model)

  suspend fun getSortOptions() {
    try {
      val options = api.sortOptions()
      if (options.isSuccessful) {
        val list = options.body()!!.result as ArrayList
        list[0].isSelected = true
        _sortList.postValue(list)
        setSelected(list[0])
      } else {
        _error.postValue(NetworkErrors.SERVER_ERROR)
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun addToWatchlist(userId: Int, coinId: Int) {
    try {
      val result = api.addWatchlist(coinId, userId)
      if (result.isSuccessful)
        _watchList.postValue(true)
      else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )

        when (error.code) {
          -1 -> _watchlistError.postValue(WatchlistErrors.INVALID_ARGS)
          -2 -> _watchlistError.postValue(WatchlistErrors.DATA_NOT_SAVED)
          -3 -> _watchlistError.postValue(WatchlistErrors.USER_NOT_FOUND)
          -4 -> _watchlistError.postValue(WatchlistErrors.COIN_NOT_FOUND)
          -5 -> _watchlistError.postValue(WatchlistErrors.SERVER_ERROR)
          else -> _watchlistError.postValue(WatchlistErrors.UNKNOWN_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _watchlistError.postValue(WatchlistErrors.NETWORK_ERROR)
      else
        _watchlistError.postValue(WatchlistErrors.UNKNOWN_ERROR)
    }
  }

  suspend fun getCoins(
    ascend: Boolean,
    sortOptions: Int,
    userId: Int = 0
  ) {
    try {
      callCoins(ascend, sortOptions, userId)
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(NetworkErrors.NETWORK_ERROR)
      else _error.postValue(NetworkErrors.UNKNOWN_ERROR)
    }
  }

  private suspend fun callCoins(ascend: Boolean, sortOptions: Int, userId: Int = 0) {
    val coins = api.allCoins(ascend, userId, sortOptions)
    if (coins.isSuccessful) {
      _coinsList.postValue(coins.body()!!.result!!)
    } else {
      when (coins.code()) {
        400 -> _error.postValue(NetworkErrors.UNKNOWN_ERROR)
        500 -> _error.postValue(NetworkErrors.SERVER_ERROR)
      }
    }
  }
}