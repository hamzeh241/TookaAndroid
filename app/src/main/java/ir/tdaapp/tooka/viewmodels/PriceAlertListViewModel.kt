package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException
import java.lang.Exception

class PriceAlertListViewModel(private val api: ApiService): ViewModel() {

  private val _alerts = MutableLiveData<List<PriceAlert>>()
  val alerts: LiveData<List<PriceAlert>>
    get() = _alerts

  private val _alertListState = MutableLiveData<AlertListState>()
  val alertListState: LiveData<AlertListState>
    get() = _alertListState

  private val _alertDisableState = MutableLiveData<AlertDisableState>()
  val alertDisableState: LiveData<AlertDisableState>
    get() = _alertDisableState

  private val _alertDeleteState = MutableLiveData<AlertDisableState>()
  val alertDeleteState: LiveData<AlertDisableState>
    get() = _alertDeleteState


  enum class AlertListState {
    OK,
    LIST_EMPTY,
    INVALID_ARGS,
    SERVER_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR
  }

  enum class AlertDisableState {
    CHANGE_SUCCESS,
    NOT_FOUND,
    DATABASE_EXCEPTION,
    UNPROCESSABLE_ENTITY,
    INVALID_ARGS,
    SERVER_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR
  }

  suspend fun getData(userId: Int) {
    try {
      val list = api.alertList(userId)
      if (list.isSuccessful) {
        _alerts.postValue(list.body()?.result!!)
      } else {
        when (list.code()) {
          400 -> _alertListState.postValue(AlertListState.INVALID_ARGS)
          404 -> _alertListState.postValue(AlertListState.LIST_EMPTY)
          500 -> _alertListState.postValue(AlertListState.SERVER_ERROR)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _alertListState.postValue(AlertListState.NETWORK_ERROR)
      else _alertListState.postValue(AlertListState.UNKNOWN_ERROR)
    }
  }

  suspend fun toggleAlert(userId: Int, alertId: Int) {
    try {
      val result = api.toggleAlert(userId, alertId)
      if (result.isSuccessful) {
        _alertDisableState.postValue(AlertDisableState.CHANGE_SUCCESS)
      } else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when (result.code()) {
          500 -> if (error.code == -3)
            _alertDisableState.postValue(AlertDisableState.DATABASE_EXCEPTION)
          else if (error.code == -5)
            _alertDisableState.postValue(AlertDisableState.SERVER_ERROR)

          404 -> _alertDisableState.postValue(AlertDisableState.NOT_FOUND)
          400 -> _alertDisableState.postValue(AlertDisableState.INVALID_ARGS)
          422 -> _alertDisableState.postValue(AlertDisableState.UNPROCESSABLE_ENTITY)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _alertDisableState.postValue(AlertDisableState.NETWORK_ERROR)
      else _alertDisableState.postValue(AlertDisableState.UNKNOWN_ERROR)
    }
  }

  suspend fun deleteAlert(userId: Int, alertId: Int) {
    try {
      val result = api.deleteAlert(userId, alertId)
      if (result.isSuccessful) {
        _alertDeleteState.postValue(AlertDisableState.CHANGE_SUCCESS)
      } else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when (result.code()) {
          500 -> if (error.code == -3)
            _alertDeleteState.postValue(AlertDisableState.DATABASE_EXCEPTION)
          else if (error.code == -5)
            _alertDeleteState.postValue(AlertDisableState.SERVER_ERROR)

          404 -> _alertDeleteState.postValue(AlertDisableState.NOT_FOUND)
          400 -> _alertDeleteState.postValue(AlertDisableState.INVALID_ARGS)
          422 -> _alertDeleteState.postValue(AlertDisableState.UNPROCESSABLE_ENTITY)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _alertDeleteState.postValue(AlertDisableState.NETWORK_ERROR)
      else _alertDeleteState.postValue(AlertDisableState.UNKNOWN_ERROR)
    }
  }
}