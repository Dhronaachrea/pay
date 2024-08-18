package com.skilrock.paypr.paypr_app.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.BalanceRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.ChangePasswordRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.LogoutRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.MyTransactionRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.*
import com.skilrock.paypr.paypr_app.repository.ProfileRepository
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.fragment_personal_data.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File


class ProfileViewModel : BaseViewModel() {

    var playerName                  = MutableLiveData(PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName())
    var mobileNumber: String        = PlayerInfo.getPlayerMobileNumber()
    var emailId                     = MutableLiveData(PlayerInfo.getPlayerEmailId())
    var playerBalance               = MutableLiveData("${PlayerInfo.getCurrencyDisplayCode()} ${PlayerInfo.getPlayerTotalBalance()}")

    var currentPassword: String     = ""
    var newPassword: String         = ""
    var confirmPassword: String     = ""

    val liveDataChangePassword      = MutableLiveData<ResponseStatus<ChangePasswordResponseData>>()
    val liveDataLogoutStatus        = MutableLiveData<ResponseStatus<LogoutResponseData>>()
    val liveDataBalanceStatus       = MutableLiveData<ResponseStatus<BalanceResponseData>>()
    val liveDataResponse            = MutableLiveData<ResponseStatus<MyTransactionResponseData>>()
    val liveDataProfileUpdateStatus = MutableLiveData<ResponseStatus<UploadAvatarResponseData>>()

    val liveDataValidationChangePassword            = MutableLiveData<ChangePasswordValidation>()
    var profileImageUrl: MutableLiveData<String>    = MutableLiveData<String>(PlayerInfo.getProfilePicPath())

    sealed class ChangePasswordValidation {
        data class OldPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
        data class NewPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
        data class ConfirmPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
    }

    fun resetBalance() {
        playerBalance.postValue("${PlayerInfo.getCurrencyDisplayCode()} ${PlayerInfo.getPlayerTotalBalance()}")
    }

    fun callTransactionApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callTransactionApi(requestTransactionData())
                withContext(Dispatchers.Main) { onMyTransactionsResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onMyTransactionsResponse(response: Response<MyTransactionResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val transactionResponse = response.body()
                    transactionResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        it.respMsg = it.respMsg?.replace(RETAILER, MERCHANT, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataResponse.postValue(ResponseStatus.Success(it))
                        else
                            liveDataResponse.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null -> liveDataResponse.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private val requestTransactionData:() -> MyTransactionRequestData = {
        MyTransactionRequestData(fromDate = getPreviousDate(30).replace("-", "/"),
            toDate = getCurrentDate().trim().replace("-", "/"), txnType = "ALL", limit = "5")
    }

    fun callBalanceApi(showLoader: Boolean = true) {
        if (showLoader)
            liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callBalanceApi(
                    BalanceRequestData(playerId = PlayerInfo.getPlayerId().toString(), playerToken = PlayerInfo.getPlayerToken())
                )
                withContext(Dispatchers.Main) { onBalanceResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onBalanceResponse(response: Response<BalanceResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val balanceResponse = response.body()
                    balanceResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        it.showToast = true
                        if (it.errorCode == 0) {
                            playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${it.wallet?.totalBalance}"
                            liveDataBalanceStatus.postValue(ResponseStatus.Success(it))
                        } else
                            liveDataBalanceStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null ->
                    liveDataBalanceStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callLogoutApi() {
        liveDataLoader.postValue(true)
        liveDataHideKeyboard.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callLogoutApi(LogoutRequestData())
                withContext(Dispatchers.Main) { onLogoutResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onLogoutResponse(response: Response<LogoutResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val logoutResponse = response.body()
                    logoutResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataLogoutStatus.postValue(ResponseStatus.Success(it))
                        else
                            liveDataLogoutStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", -1))
                    }
                }

                response.errorBody() != null ->
                    liveDataLogoutStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun validateInputs(): Boolean {
        if (currentPassword.trim().isBlank() || currentPassword.trim().length < 8) {
            liveDataValidationChangePassword.postValue(ChangePasswordValidation.OldPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (newPassword.trim().isBlank() || newPassword.trim().length < 8) {
            liveDataValidationChangePassword.postValue(ChangePasswordValidation.NewPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (confirmPassword.trim().isBlank() || confirmPassword.trim().length < 8) {
            liveDataValidationChangePassword.postValue(ChangePasswordValidation.ConfirmPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (currentPassword.trim().equals(newPassword.trim(),true)) {
            liveDataValidationChangePassword.postValue(ChangePasswordValidation.NewPasswordValidation(R.string.old_and_new_password_cannot_be_same))
            return false
        }

        if (!newPassword.trim().equals(confirmPassword.trim(),true)) {
            liveDataValidationChangePassword.postValue(ChangePasswordValidation.ConfirmPasswordValidation(R.string.passwords_do_not_match))
            return false
        }

        return true
    }

    fun callChangePasswordApi() {
        liveDataHideKeyboard.postValue(true)
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = ProfileRepository().callChangePassword(requestChangePassword())
                    withContext(Dispatchers.Main) { onChangePasswordResponse(response) }
                } catch (e: java.lang.Exception) {
                    if (e is NoConnectivityException)
                        liveDataChangePassword.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataChangePassword.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                } finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onChangePasswordResponse(response: Response<ChangePasswordResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val changePasswordResponseData = response.body()
                    changePasswordResponseData?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataChangePassword.postValue(ResponseStatus.Success(it))
                        else
                            liveDataChangePassword.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null ->
                    liveDataChangePassword.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataChangePassword.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: java.lang.Exception) {
            liveDataChangePassword.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private val requestChangePassword: () -> ChangePasswordRequestData = {
        ChangePasswordRequestData(oldPassword = currentPassword, newPassword = newPassword)
    }

    fun onProfileUpload(path: String) {
        liveDataLoader.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ProfileRepository().callUpdateAvatarApi(uploadAvatarRequestParams(), imageMultiPartFromFile(path))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "UploadAvatar Response: ${Gson().toJson(response.body())}")
                                response.body()?.let {
                                    it.showToast = true
                                    if (it.errorCode == 0) {
                                        it.avatarPath?.let { path ->
                                            profileImageUrl.value = path
                                            liveDataProfileUpdateStatus.postValue(ResponseStatus.Success(it))
                                        }
                                    }
                                    else
                                        liveDataProfileUpdateStatus.postValue(ResponseStatus.Error(it.respMsg ?: "Technical Issue", it.errorCode))
                                }
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "UploadAvatar Response: $response")
                                liveDataProfileUpdateStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                            }
                            else -> {
                                Log.e("log", "UploadAvatar Response: $response")
                                liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "UploadAvatar Response: $response")
                        liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.failed_to_upload_image))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun imageMultiPartFromFile(path: String): MultipartBody.Part {
        val file = File(path)
        return MultipartBody.Part.createFormData("imageFile", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
    }

    private fun uploadAvatarRequestParams(): HashMap<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        map["playerToken"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), PlayerInfo.getPlayerToken())
        map["domainName"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), BuildConfig.DOMAIN_NAME)
        map["playerId"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), PlayerInfo.getPlayerId().toString())
        map["imageFileName"] = RequestBody.create(
            MediaType.parse(PARSE_TYPE),
            PlayerInfo.getPlayerId().toString() + "_" + PlayerInfo.getPlayerFirstName()
        )
        map["isDefaultAvatar"] = RequestBody.create(MediaType.parse(PARSE_TYPE), "\"Y\"")
        return map
    }

    companion object {
        @JvmStatic
        @BindingAdapter("profileImageUrl")
        fun loadImage(view: ImageView, profileImageUrl: String) =
            Glide.with(view.context)
                .load(profileImageUrl)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(view.ivProfileImage)
    }

}

