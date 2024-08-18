package com.skilrock.paypr.paypr_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.UpdateProfileRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.UpdateProfileResponseData
import com.skilrock.paypr.paypr_app.repository.ProfileRepository
import com.skilrock.paypr.utility.CONSUMER
import com.skilrock.paypr.utility.PLAYER
import com.skilrock.paypr.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class ProfileUpdateViewModel : BaseViewModel() {

    val liveDataUpdateProfile   = MutableLiveData<ResponseStatus<UpdateProfileResponseData>>()

    fun callProfileUpdateApi(firstName: String, lastName: String, email: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callProfileUpdateApi(getProfileUpdateRequestData(firstName, lastName, email))
                withContext(Dispatchers.Main) { onProfileUpdateResponse(response) }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NoConnectivityException)
                    liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onProfileUpdateResponse(response: Response<UpdateProfileResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val profileUpdateResponse = response.body()
                    profileUpdateResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataUpdateProfile.postValue(ResponseStatus.Success(it))
                        else
                            liveDataUpdateProfile.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null ->
                    liveDataUpdateProfile.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun getProfileUpdateRequestData(firstName: String, lastName: String, email: String) : UpdateProfileRequestData {
        return UpdateProfileRequestData(firstName=firstName, lastName=lastName, emailId=email)
    }

}

