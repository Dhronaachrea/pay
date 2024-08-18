package com.skilrock.paypr.paypr_app.viewmodel

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.LoginRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.repository.LoginRepository
import com.skilrock.paypr.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginViewModel : BaseViewModel() {

    private var lastClickTime: Long = 0
    private var clickGap        = 600

    var mobileNumber: String    = ""
    var password: String        = ""
    val liveDataValidation      = MutableLiveData<Validation>()
    val liveDataLoginStatus     = MutableLiveData<ResponseStatus<LoginResponseData>>()

    sealed class Validation {
        data class UserNameValidation(val errorMessageCode: Int) : Validation()
        data class PasswordValidation(val errorMessageCode: Int) : Validation()
    }

    fun onLoginButtonClick(fcmToken: String) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        liveDataHideKeyboard.postValue(true)
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = LoginRepository().performLogin(loginRequestData(fcmToken))
                    withContext(Dispatchers.Main) { onLoginResponseReceived(response) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is NoConnectivityException)
                        liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onLoginResponseReceived(response: Response<LoginResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val loginResponse: LoginResponseData? = response.body()
                    loginResponse?.respMsg = loginResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    loginResponse?.respMsg = loginResponse?.respMsg?.replace(RETAILER, MERCHANT, ignoreCase = true)
                    if (loginResponse?.errorCode == 0)
                        liveDataLoginStatus.postValue(ResponseStatus.Success(loginResponse))
                    else
                        liveDataLoginStatus.postValue(ResponseStatus.Error(loginResponse?.respMsg ?: ""))
                }

                response.errorBody() != null -> liveDataLoginStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun validateInputs(): Boolean {
        if (mobileNumber.trim().isEmpty() || mobileNumber.trim().length < BuildConfig.MOBILE_NO_LENGTH) {
            liveDataValidation.postValue(Validation.UserNameValidation(R.string.invalid_mobile_number))
            return false
        }

        if (password.trim().isEmpty() || password.trim().length < 8) {
            liveDataValidation.postValue(Validation.PasswordValidation(R.string.password_eight_characters))
            return false
        }

        if (encryptField(password).isEmpty()) {
            liveDataValidation.postValue(Validation.PasswordValidation(R.string.password_encrypt_error))
            return false
        }

        return true
    }

    private fun encryptField(password: String) : String {
        val firstEncrypt = encryption(password)
        return encryption(firstEncrypt + LOGIN_TOKEN)
    }

    private fun loginRequestData(fcmToken: String) : LoginRequestData  {
        return LoginRequestData(DEVICE_TYPE, BuildConfig.DOMAIN_NAME, LOGIN_DEVICE, LOGIN_TOKEN, encryptField(password), REQUEST_IP, "",
            System.getProperty("http.agent"),
            mobileNumber,
            fcmToken)
    }

}