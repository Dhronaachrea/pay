package com.skilrock.paypr.paypr_app.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.LastLoginRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LastLoginResponseData
import com.skilrock.paypr.paypr_app.repository.GreetingRepository
import com.skilrock.paypr.utility.CONSUMER
import com.skilrock.paypr.utility.PLAYER
import com.skilrock.paypr.utility.PlayerInfo
import com.skilrock.paypr.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GreetingViewModel : BaseViewModel() {

    var greet   = getGreetTag() + " " + PlayerInfo.getPlayerFirstName()
    var balance = "${PlayerInfo.getCurrencyDisplayCode()} ${PlayerInfo.getPlayerTotalBalance()}"

    val liveDataVersionStatus = MutableLiveData<ResponseStatus<LastLoginResponseData>>()

    fun callLastLoginDetailApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<LastLoginResponseData> = GreetingRepository().callLastLoginDetailApi(LastLoginRequestData())
                withContext(Dispatchers.Main) { onLastLoginResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onLastLoginResponse(response: Response<LastLoginResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val lastLoginResponse = response.body()
                    lastLoginResponse?.let {
                        it.respMsg = it.respMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataVersionStatus.postValue(ResponseStatus.Success(it))
                        else
                            liveDataVersionStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null -> liveDataVersionStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))

                else -> liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun getGreetTag() : String {
        val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hourOfDay < 12 -> "Good Morning"
            hourOfDay < 16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(strDate: String) : String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("MMM d, yyyy")
            strDate.let { tranDate ->
                parser.parse(tranDate)?.let { date ->
                    formatter.format(date)
                }
            } ?: "NA"
        } catch (e: Exception) {
            "NA"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(strDate: String) : String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("hh:mm aa")
            strDate.let { tranDate ->
                parser.parse(tranDate)?.let { date ->
                    formatter.format(date)
                }
            } ?: "NA"
        } catch (e: Exception) {
            "NA"
        }
    }
}