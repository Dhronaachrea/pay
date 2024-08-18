package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.BalanceRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.FundTransferFromWalletRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.FundTransferFromWalletResponseData
import retrofit2.Response

class FundTransferFromWalletRepository {

    suspend fun sendMoney(body: FundTransferFromWalletRequestData) : Response<FundTransferFromWalletResponseData> {
        return RetrofitNetworking.create().callFundTransferFromWalletApi(body)
    }

    suspend fun callBalanceApi(balanceRequestData: BalanceRequestData) : Response<BalanceResponseData> {
        return RetrofitNetworking.create().callUpdateBalanceApi(balanceRequestData)
    }

}