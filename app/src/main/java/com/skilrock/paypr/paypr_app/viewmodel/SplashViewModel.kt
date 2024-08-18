package com.skilrock.paypr.paypr_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.VersionRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.VersionResponseData
import com.skilrock.paypr.paypr_app.repository.SplashRepository
import com.skilrock.paypr.utility.CONSUMER
import com.skilrock.paypr.utility.PLAYER
import com.skilrock.paypr.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SplashViewModel : BaseViewModel() {

    val liveDataVersionStatus = MutableLiveData<ResponseStatus<VersionResponseData>>()

    fun callVersionApi(appVersion: String, playerToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<VersionResponseData> = SplashRepository().callVersionApi(getRequestData(appVersion, playerToken))
                withContext(Dispatchers.Main) { onVersionResponse(response) }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NoConnectivityException)
                    liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onVersionResponse(response: Response<VersionResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val versionResponse = response.body()
                    versionResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataVersionStatus.postValue(ResponseStatus.Success(it))
                        else
                            liveDataVersionStatus.postValue(ResponseStatus.Error(it.respMsg ?: ""))
                    }
                }

                response.errorBody() != null ->
                    liveDataVersionStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun getRequestData(appVersion: String, plrToken: String) : VersionRequestData {
        return VersionRequestData(currAppVer = appVersion, playerToken = plrToken)
    }

}