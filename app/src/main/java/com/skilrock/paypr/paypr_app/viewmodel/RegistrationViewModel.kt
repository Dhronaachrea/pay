package com.skilrock.paypr.paypr_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.LoginRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationOtpRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationValidationRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationOtpResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationValidationResponseData
import com.skilrock.paypr.paypr_app.repository.RegistrationRepository
import com.skilrock.paypr.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegistrationViewModel : BaseViewModel() {

    var firstName: String           = ""
    var lastName: String            = ""
    var name: String                = ""
    var mobileNumber: String        = ""
    var email: String               = ""
    var password: String            = ""
    var confirmPassword: String     = ""
    var otp: String                 = ""
    private var referCode: String   = ""
    val liveDataValidation          = MutableLiveData<Validation>()
    val liveDataRegistrationStatus  = MutableLiveData<ResponseStatus<RegistrationResponseData>>()
    val liveDataRegistrationOtp     = MutableLiveData<ResponseStatus<RegistrationOtpResponseData>>()
    val liveDataRegistrationValid   = MutableLiveData<ResponseStatus<RegistrationValidationResponseData>>()
    val liveDataLoginStatus         = MutableLiveData<ResponseStatus<LoginResponseData>>()

    sealed class Validation {
        data class UserName(val errorMessageCode: Int) : Validation()
        data class MobileNumber(val errorMessageCode: Int) : Validation()
        data class Email(val errorMessageCode: Int) : Validation()
        data class Password(val errorMessageCode: Int) : Validation()
        data class ConfirmPassword(val errorMessageCode: Int) : Validation()
    }

    fun onRegisterButtonClick() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RegistrationRepository().registerPlayer(registrationRequestData())
                withContext(Dispatchers.Main) { onRegistrationResponse(response) }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NoConnectivityException)
                    liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onRegistrationResponse(response: Response<RegistrationResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val registrationResponse = response.body()
                    registrationResponse?.respMsg = registrationResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    if (registrationResponse?.errorCode == 0)
                        liveDataRegistrationStatus.postValue(ResponseStatus.Success(registrationResponse))
                    else
                        liveDataRegistrationStatus.postValue(ResponseStatus.Error(registrationResponse?.respMsg ?: ""))
                }

                response.errorBody() != null -> liveDataRegistrationStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callRegistrationOtpApi() {
        liveDataHideKeyboard.postValue(true)
        if (validateOtpInputs()) {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = RegistrationRepository().registrationOtp(registrationRequestOtpData())
                    withContext(Dispatchers.Main) { onRegistrationOtpResponseReceived(response) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is NoConnectivityException)
                        liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onRegistrationOtpResponseReceived(response: Response<RegistrationOtpResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val otpResponse = response.body()
                    otpResponse?.respMsg = otpResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    if (otpResponse?.errorCode == 0)
                        liveDataRegistrationOtp.postValue(ResponseStatus.Success(otpResponse))
                    else
                        liveDataRegistrationOtp.postValue(ResponseStatus.Error(otpResponse?.respMsg ?: ""))
                }

                response.errorBody() != null -> liveDataRegistrationOtp.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun validateOtpInputs() : Boolean {
        if (email.trim().isEmpty()) {
            liveDataValidation.postValue(Validation.Email(R.string.enter_email))
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            liveDataValidation.postValue(Validation.Email(R.string.enter_valid_email))
            return false
        }

        if (mobileNumber.trim().isEmpty()) {
            liveDataValidation.postValue(Validation.MobileNumber(R.string.enter_mobile_number))
            return false
        }

        if (mobileNumber.trim().length < BuildConfig.MOBILE_NO_LENGTH) {
            liveDataValidation.postValue(Validation.MobileNumber(R.string.enter_valid_mobile_number))
            return false
        }

        if (name.trim().isBlank()) {
            liveDataValidation.postValue(Validation.UserName(R.string.name_cannot_be_empty))
            return false
        }

        if (password.trim().isEmpty() || password.trim().length < 8) {
            liveDataValidation.postValue(Validation.Password(R.string.password_eight_characters))
            return false
        }

        if (password != confirmPassword) {
            liveDataValidation.postValue(Validation.ConfirmPassword(R.string.passwords_do_not_match))
            return false
        }

        return true
    }

    private val registrationRequestOtpData:() -> RegistrationOtpRequestData = {

        val fullNameList: List<String> = name.trim().split(" ", limit = 2)

        when {
            fullNameList.isNullOrEmpty() -> {
                firstName   = name
                lastName    = ""
            }
            fullNameList.size == 1 -> {
                firstName   = fullNameList[0]
                lastName    = ""
            }
            else -> {
                firstName   = fullNameList[0]
                lastName    = fullNameList[1]
            }
        }

        RegistrationOtpRequestData(firstName = firstName, lastName= lastName, mobileNo = mobileNumber.toLong(),
            password = password, userName = mobileNumber, referCode = referCode, emailId = email)
    }

    private val registrationRequestData:() -> RegistrationRequestData = {
        val fullNameList: List<String> = name.trim().split(" ")

        when {
            fullNameList.isNullOrEmpty() -> {
                firstName   = name
                lastName    = ""
            }
            fullNameList.size == 1 -> {
                firstName   = fullNameList[0]
                lastName    = ""
            }
            else -> {
                firstName   = fullNameList[0]
                lastName    = fullNameList[1]
            }
        }

        RegistrationRequestData(firstName = firstName, lastName= lastName, mobileNo = mobileNumber,
            password = password, userName = mobileNumber, referCode = referCode, otp = otp, emailId=email)
    }

    fun callRegistrationValidationApi() {
        liveDataHideKeyboard.postValue(true)
        if (validateOtpInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = RegistrationRepository().registrationValidation(getValidationRequestData())
                    withContext(Dispatchers.Main) { onRegistrationValidationResponseReceived(response) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    liveDataLoader.postValue(false)
                    if (e is NoConnectivityException)
                        liveDataRegistrationValid.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataRegistrationValid.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
            }
        }
    }

    private fun onRegistrationValidationResponseReceived(response: Response<RegistrationValidationResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val validationResponse = response.body()
                    validationResponse?.respMsg = validationResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    if (validationResponse?.errorCode == 0)
                        liveDataRegistrationValid.postValue(ResponseStatus.Success(validationResponse))
                    else {
                        liveDataLoader.postValue(false)
                        liveDataRegistrationValid.postValue(ResponseStatus.Error(validationResponse?.respMsg ?: ""))
                    }
                }
                response.errorBody() != null -> {
                    liveDataLoader.postValue(false)
                    liveDataRegistrationValid.postValue(ResponseStatus.Error(response.errorBody().toString()))
                }
                else -> {
                    liveDataLoader.postValue(false)
                    liveDataRegistrationValid.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                }
            }
        } catch (e: Exception) {
            liveDataLoader.postValue(false)
            liveDataRegistrationValid.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun getValidationRequestData() : RegistrationValidationRequestData {
        return RegistrationValidationRequestData(emailid = email.trim(), mobileNo = mobileNumber.trim(), userName = mobileNumber.trim())
    }

    fun callLoginApi(fcmToken: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RegistrationRepository().performLogin(loginRequestData(fcmToken))
                withContext(Dispatchers.Main) { onLoginResponse(response) }
            } catch (e: Exception) {
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

    private fun onLoginResponse(response: Response<LoginResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val loginResponse = response.body()
                    loginResponse?.respMsg = loginResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
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

    private fun loginRequestData(fcmToken: String) : LoginRequestData  {
        return LoginRequestData(DEVICE_TYPE, BuildConfig.DOMAIN_NAME,
            LOGIN_DEVICE, LOGIN_TOKEN, encryptField(password), REQUEST_IP, "",
            System.getProperty("http.agent"), mobileNumber, fcmToken)
    }

    private fun encryptField(password: String) : String {
        val firstEncrypt = encryption(password)
        return encryption(firstEncrypt + LOGIN_TOKEN)
    }
}