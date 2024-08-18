package com.skilrock.paypr.paypr_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.MyTransactionRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.MyTransactionResponseData
import com.skilrock.paypr.paypr_app.repository.ProfileRepository
import com.skilrock.paypr.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class TransactionViewModel : BaseViewModel() {

    var startDate: String   = getPreviousDate(30)
    var endDate: String     = getCurrentDate()
    val liveDataResponse    = MutableLiveData<ResponseStatus<MyTransactionResponseData>>()

    fun callTransactionApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callTransactionApi(requestData())
                withContext(Dispatchers.Main) { onMyTransactionsResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
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

                response.errorBody() != null ->
                    liveDataResponse.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private val requestData:() -> MyTransactionRequestData = {
        MyTransactionRequestData(fromDate = startDate.replace("-", "/"),
            toDate = endDate.trim().replace("-", "/"), txnType = "ALL")
    }
}

