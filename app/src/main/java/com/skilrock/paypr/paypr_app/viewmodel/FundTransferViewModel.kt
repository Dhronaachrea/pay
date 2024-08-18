package com.skilrock.paypr.paypr_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.paypr.R
import com.skilrock.paypr.network.NoConnectivityException
import com.skilrock.paypr.paypr_app.data_class.request_data.BalanceRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.FundTransferFromWalletRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.FundTransferFromWalletResponseData
import com.skilrock.paypr.paypr_app.repository.FundTransferFromWalletRepository
import com.skilrock.paypr.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class FundTransferViewModel : BaseViewModel() {

    var amount: String              = ""
    var destinationAccount: String  = ""
    var merchantId: String          = ""
    val liveDataValidateAmount      = MutableLiveData<Int>()
    val liveDataBalanceStatus       = MutableLiveData<ResponseStatus<BalanceResponseData>>()
    val liveDataFundTransferStatus  = MutableLiveData<ResponseStatus<FundTransferFromWalletResponseData>>()

    fun onPayClick() {
        liveDataHideKeyboard.postValue(true)
        if (validate()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = FundTransferFromWalletRepository().sendMoney(getRequestData())
                    withContext(Dispatchers.Main) { onFundTransferResponse(response) }
                } catch (e: Exception) {
                    if (e is NoConnectivityException)
                        liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                    else
                        liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onFundTransferResponse(response: Response<FundTransferFromWalletResponseData>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val fundTransferResponse = response.body()
                    fundTransferResponse?.let {
                        it.errorMsg = it.errorMsg?.replace(PLAYER, CONSUMER, ignoreCase = true)
                        it.errorMsg = it.errorMsg?.replace(RETAILER, MERCHANT, ignoreCase = true)
                        if (it.errorCode == 0)
                            liveDataFundTransferStatus.postValue(ResponseStatus.Success(it))
                        else
                            liveDataFundTransferStatus.postValue(ResponseStatus.Error(it.errorMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null -> liveDataFundTransferStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun validate() : Boolean {
        if (amount.trim().isBlank()) {
            liveDataValidateAmount.postValue(R.string.enter_amount)
            return false
        }

        if (amount.trim().contains(".")) {
            Log.d("log", "Double Value")
            try {
                val amt = amount.trim().toDouble()
                if (amt == 0.0) {
                    liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                    return false
                }
            } catch (e: Exception) {
                liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                return false
            }
        }
        else {
            Log.d("log", "Integer Value")
            try {
                val amt = amount.trim().toInt()
                if (amt == 0) {
                    liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                    return false
                }
            } catch (e: Exception) {
                liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                return false
            }
        }

        return true
    }

    private fun getRequestData(): FundTransferFromWalletRequestData {
        return FundTransferFromWalletRequestData(amount = amount , destinationAccount = destinationAccount, merchantId = merchantId)
    }

    fun callBalanceApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FundTransferFromWalletRepository().callBalanceApi(
                    BalanceRequestData(playerId = PlayerInfo.getPlayerId().toString(), playerToken = PlayerInfo.getPlayerToken())
                )
                withContext(Dispatchers.Main) { onBalanceResponse(response) }
            } catch (e: Exception) {
                if (e is NoConnectivityException)
                    liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                else
                    liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }  finally {
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
                        if (it.errorCode == 0) {
                            liveDataBalanceStatus.postValue(ResponseStatus.Success(it))
                        } else
                            liveDataBalanceStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
                    }
                }

                response.errorBody() != null -> liveDataBalanceStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))

                else -> liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

}