package com.skilrock.paypr.paypr_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData
import com.skilrock.paypr.paypr_app.repository.MerchantRepository
import com.skilrock.paypr.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MerchantViewModel : BaseViewModel() {

    val liveDataMerchant = MutableLiveData<ResponseStatus<MerchantLocationResponseData>>()

    fun callMerchantApi(latitude: Double, longitude: Double, radius: Double) {
        //latitude: Double = 26.944081660583073, longitude: Double = 77.81761727911353, radius: Double = 1000.0
        liveDataLoader.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<MerchantLocationResponseData> = MerchantRepository().callRetailerLocationApi(RMS_CLIENT_TOKEN, latitude, longitude, radius)
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null ->
                                onMerchantApiResponse(response.body())

                            response.errorBody() != null ->
                                liveDataMerchant.postValue(ResponseStatus.Error(response.errorBody().toString()))

                            else ->
                                liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Merchant Location Response: $response, ${e.message}")
                        liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    } finally {
                        liveDataLoader.postValue(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NoConnectivityException)
                    liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onMerchantApiResponse(response: MerchantLocationResponseData?) {
        response?.let {
            it.responseMessage = it.responseMessage?.replace(PLAYER, CONSUMER, ignoreCase = true)
            it.responseMessage = it.responseMessage?.replace(RETAILER, MERCHANT, ignoreCase = true)
            if (it.responseCode == 0) {
                it.responseData?.let { responseData ->
                    responseData.message = responseData.message?.replace(PLAYER, CONSUMER, ignoreCase = true)
                    responseData.message = responseData.message?.replace(RETAILER, MERCHANT, ignoreCase = true)
                    if (responseData.statusCode == 0) {
                        responseData.data?.let { _ ->
                            liveDataMerchant.postValue(ResponseStatus.Success(it))
                        } ?: run { liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.some_internal_error)) }
                    } else
                        liveDataMerchant.postValue(ResponseStatus.Error(responseData.message ?: "", responseData.statusCode ?: -1))
                } ?: run { liveDataMerchant.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong)) }
            }
            else
                liveDataMerchant.postValue(ResponseStatus.Error(it.responseMessage ?: ""))
        }
    }
}