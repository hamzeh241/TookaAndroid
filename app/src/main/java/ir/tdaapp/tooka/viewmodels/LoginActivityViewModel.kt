package ir.tdaapp.tooka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.UserErrors
import ir.tdaapp.tooka.util.api.ApiService
import java.io.IOException

class LoginActivityViewModel(private val api: ApiService): ViewModel() {

  private val _loginResponse = MutableLiveData<Boolean>()
  val loginResponse: LiveData<Boolean>
    get() = _loginResponse

  private val _resendResponse = MutableLiveData<Boolean>()
  val resendResponse: LiveData<Boolean>
    get() = _resendResponse

  private val _googleLogin = MutableLiveData<Boolean>()
  val googleLogin: LiveData<Boolean>
    get() = _googleLogin

  private val _verification = MutableLiveData<UserInfoResponse>()
  val verification: LiveData<UserInfoResponse>
    get() = _verification

  private val _error = MutableLiveData<UserErrors>()
  val error: LiveData<UserErrors>
    get() = _error

  private val _verificationCode = MutableLiveData<Int>()
  val verificationCode: LiveData<Int>
    get() = _verificationCode

  private val _phoneNumber = MutableLiveData<String>()
  val phoneNumber: LiveData<String>
    get() = _phoneNumber

  fun setPhoneNumber(phone: String) {
    _phoneNumber.postValue(phone)
  }

  suspend fun loginOrSignup(phone: String) {
    try {
      val response = api.loginOrSignup(phone)
      if (response.isSuccessful)
        _loginResponse.postValue(response.body()!!.result!!)
      else {
        val error = Gson().fromJson(
          response.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when (error.code) {
          -1 -> _error.postValue(UserErrors.INVALID_ARGS_EXCEPTION)

          -2 -> _error.postValue(UserErrors.SMS_EXCEPTION)

          -3 -> _error.postValue(UserErrors.DATABASE_EXCEPTION)

          else -> _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(UserErrors.NETWORK_EXCEPTION)
      else _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
    }
  }

  suspend fun googleLogin(model: GoogleLoginModel) {
    try {
      val response = api.loginOrSignupGoogle(model)
      if (response.isSuccessful)
        _googleLogin.postValue(response.body()!!.result!!)
      else {
        val error = Gson().fromJson(
          response.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when (error.code) {
          -1 -> _error.postValue(UserErrors.INVALID_ARGS_EXCEPTION)

          -2 -> _error.postValue(UserErrors.NOT_FOUND)

          -3 -> _error.postValue(UserErrors.SMS_EXCEPTION)

          -4 -> _error.postValue(UserErrors.DATABASE_EXCEPTION)

          else -> _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(UserErrors.NETWORK_EXCEPTION)
      else _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
    }
  }

  suspend fun verify(phone: String, code: Int) {
    try {
      val auth = api.verify(LoginModel(phone, code))
      if (auth.isSuccessful)
        _verification.postValue(auth.body()!!.result!!)
      else {
        val error = Gson().fromJson(
          auth.errorBody()?.string(),
          ResponseModel
          ::class.java
        )

        when (error.code) {
          -1 -> _error.postValue(UserErrors.INVALID_ARGS_EXCEPTION)

          -2 -> _error.postValue(UserErrors.UNKNOWN_EXCEPTION)

          -3 -> _error.postValue(UserErrors.DATABASE_EXCEPTION)

          -4 -> _error.postValue(UserErrors.INVALID_CODE)

          else -> _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(UserErrors.NETWORK_EXCEPTION)
      else _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
    }
  }

  suspend fun resendSms(phone: String) {
    try {
      val result = api.resendSms(phone)
      if (result.isSuccessful) {
        _resendResponse.postValue(result.body()?.result!!)
      } else {
        val error = Gson().fromJson(
          result.errorBody()?.string(),
          ResponseModel
          ::class.java
        )
        when(error.code){
          -1 -> _error.postValue(UserErrors.INVALID_ARGS_EXCEPTION)
          -2 -> _error.postValue(UserErrors.DATABASE_EXCEPTION)
          -3 -> _error.postValue(UserErrors.SMS_EXCEPTION)
          -4 -> _error.postValue(UserErrors.NOT_FOUND)
          -5 -> _error.postValue(UserErrors.CODE_TOO_EARLY_TO_SEND)
          else -> _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
        }
      }
    } catch (e: Exception) {
      if (e is IOException)
        _error.postValue(UserErrors.NETWORK_EXCEPTION)
      else _error.postValue(UserErrors.UNKNOWN_EXCEPTION)
    }
  }

  fun recieveVerificationCode(code: Int) = _verificationCode.postValue(code)
}