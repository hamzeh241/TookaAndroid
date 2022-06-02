package ir.tdaapp.tooka.models.network

import com.google.gson.Gson
import ir.tdaapp.tooka.models.dataclasses.ResponseModel
import ir.tdaapp.tooka.models.util.NetworkErrors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

suspend fun <T> callApi(
  dispatcher: CoroutineDispatcher = Dispatchers.IO,
  apiCall: suspend ()->Response<T>
): T {
  return withContext(dispatcher) {
    try {
      val response = apiCall.invoke()
      if (response.isSuccessful) {
        response.body()!!
      } else {
        response.errorBody()?.let {
          Gson().fromJson(it.string(), ResponseModel::class.java)
        } as T
      }
    } catch (throwable: Throwable) {
      when (throwable) {
        is IOException -> ResponseModel(-1, "", null, false, NetworkErrors.NETWORK_ERROR) as T
        else -> ResponseModel(-1, "", null, false, NetworkErrors.UNKNOWN_ERROR) as T
      }
    }
  }
}