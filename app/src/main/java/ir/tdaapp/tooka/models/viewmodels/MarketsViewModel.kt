package ir.tdaapp.tooka.models.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.tdaapp.tooka.models.dataclasses.AddWatchlistResult
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.SortModel
import ir.tdaapp.tooka.models.repositories.MarketsRepository
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.util.WatchlistErrors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MarketsViewModel(private val repository: MarketsRepository): ViewModel() {

  private val _coinsList = MutableLiveData<List<Coin>>()
  val coinsList: LiveData<List<Coin>>
    get() = _coinsList

  private val _sortList = MutableLiveData<List<SortModel>>()
  val sortList: LiveData<List<SortModel>>
    get() = _sortList

  private val _watchList = MutableLiveData<AddWatchlistResult>()
  val watchList: LiveData<AddWatchlistResult>
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

  init {
    viewModelScope.launch(Dispatchers.IO) {
      getSortOptions()
    }
    viewType.value = ViewType.Linear
  }

  fun setSortOptions(options:List<SortModel>){
    _sortList.value = options
    setSelected(options.firstOrNull{ it.isSelected }!!)
  }

  suspend fun getSortOptions() {
    val options = repository.getSortOptions()
    if (options.status) {
      val list = options.result as ArrayList
      list[0].isSelected = true
      _sortList.postValue(list)
      setSelected(list[0])
    } else {
      _error.postValue(options.errorType)
    }
  }

  suspend fun addWatchlist(userId: Int, coinId: Int) {
    val (result, error) = repository.addWatchlist(userId, coinId)
    if (result.status) {
      _watchList.postValue(result.result!!)
    } else _watchlistError.postValue(error)
  }

  suspend fun getCoins(
    ascend: Boolean,
    sortOptions: Int,
    userId: Int = 0,
    refresh: Boolean = false
  ) {
    if (repository.isEmpty()) {
      val result = repository.getData(ascend, sortOptions, userId)
      if (result.status) {
        _coinsList.postValue(result.result!!)
        repository.addToDatabase(result.result)
      } else _error.postValue(result.errorType)
    } else {
      if (!refresh) {
        val local = repository.getLocalData().firstOrNull() ?: emptyList()
        _coinsList.postValue(local)
        val remote = repository.getData(ascend, sortOptions, userId)
        if (remote.status) {
          _coinsList.postValue(remote.result!!)
          repository.updateDatabase(remote.result)
        } else _error.postValue(remote.errorType)
      } else {
        val result = repository.getData(ascend, sortOptions, userId)
        if (result.status) {
          _coinsList.postValue(result.result!!)
          repository.updateDatabase(result.result)
        } else _error.postValue(result.errorType)
      }
    }
  }

  fun setSelected(model: SortModel) = _selectedSort.postValue(model)

}