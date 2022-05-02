package ir.tdaapp.tooka.models.dataclasses

import ir.tdaapp.tooka.models.util.Status

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

  companion object {

    fun <T> success(data: T?): Resource<T> {
      return Resource(Status.SUCCESS, data, null)
    }

    fun <T> error(error: Status, msg: String, data: T?): Resource<T> {
      return Resource(error, data, msg)
    }

    fun <T> loading(data: T?): Resource<T> {
      return Resource(Status.LOADING, data, null)
    }

  }

}