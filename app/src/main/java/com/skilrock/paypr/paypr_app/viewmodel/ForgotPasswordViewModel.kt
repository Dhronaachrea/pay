package com.skilrock.paypr.paypr_app.viewmodel

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.ForgotPasswordRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.ForgotPasswordResponseData
import com.skilrock.paypr.paypr_app.repository.ForgotPasswordRepository
import com.skilrock.paypr.utility.CONSUMER
import com.skilrock.paypr.utility.PLAYER
import com.skilrock.paypr.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ForgotPasswordViewModel : BaseViewModel() {

    private var lastClickTime: Long     = 0
    private var clickGap                = 600
    var mobileNumber: String            = ""
    val liveDataValidateMobileNumber    = MutableLiveData<Int>()
    val liveDataOtpStatus               = MutableLiveData<ResponseStatus<ForgotPasswordResponseData>>()
    val liveDataResetPasswordStatus     = MutableLiveData<ResponseStatus<ForgotPasswordResponseData>>()

    fun sendOtp(openDialog: Boolean) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        liveDataVibrator.postValue("")
        liveDataHideKeyboard.postValue(true)
        if (validateMobileNumber()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = ForgotPasswordRepository().sendOtp(ForgotPasswordRequestData(mobileNo = mobileNumber))
                    withContext(Dispatchers.Main) { onOtpResponseReceived(response, openDialog) }
                } catch (e: Exception) {
                    if (e is NoConnectivityException)
                        liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onOtpResponseReceived(response: Response<ForgotPasswordResponseData>, openDialog: Boolean) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val otpResponse = response.body()
                    otpResponse?.respMsg = otpResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    if (otpResponse?.errorCode == 0) {
                        otpResponse.openDialog = openDialog
                        liveDataOtpStatus.postValue(ResponseStatus.Success(otpResponse))
                    }
                    else
                        liveDataOtpStatus.postValue(ResponseStatus.Error(otpResponse?.respMsg ?: ""))
                }

                response.errorBody() != null -> liveDataOtpStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun validateMobileNumber() : Boolean {
        if (mobileNumber.trim().isEmpty()) {
            liveDataValidateMobileNumber.postValue(R.string.enter_mobile_number)
            return false
        }

        if (mobileNumber.trim().length < BuildConfig.MOBILE_NO_LENGTH) {
            liveDataValidateMobileNumber.postValue(R.string.enter_valid_mobile_number)
            return false
        }

        return true
    }

    fun resetPassword(otp: String, newPassword: String, confirmPassword: String) {
        liveDataHideKeyboard.postValue(true)
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ForgotPasswordRepository().resetPassword(getForgotPasswordRequestData(otp, newPassword, confirmPassword))
                withContext(Dispatchers.Main) { onResetPasswordResponseReceived(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onResetPasswordResponseReceived(response: Response<ForgotPasswordResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val resetPasswordResponse = response.body()
                    resetPasswordResponse?.respMsg = resetPasswordResponse?.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    if (resetPasswordResponse?.errorCode == 0)
                        liveDataResetPasswordStatus.postValue(ResponseStatus.Success(resetPasswordResponse))
                    else
                        liveDataResetPasswordStatus.postValue(ResponseStatus.Error(resetPasswordResponse?.respMsg ?: ""))
                }

                response.errorBody() != null ->
                    liveDataResetPasswordStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else ->
                    liveDataResetPasswordStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataResetPasswordStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun getForgotPasswordRequestData(otpParam: String, newPasswordParam: String,
                                             confirmPasswordParam: String) : ForgotPasswordRequestData  {
        return ForgotPasswordRequestData(otp = otpParam, newPassword = newPasswordParam,
            confirmPassword = confirmPasswordParam, mobileNo = mobileNumber)
    }

}