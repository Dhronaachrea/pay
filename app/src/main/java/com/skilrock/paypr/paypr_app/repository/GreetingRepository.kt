package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.LastLoginRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LastLoginResponseData
import retrofit2.Response

class GreetingRepository {

    suspend fun callLastLoginDetailApi(lastLoginRequestData: LastLoginRequestData) : Response<LastLoginResponseData> {
        return RetrofitNetworking.create().callLastLoginApi(lastLoginRequestData)
    }

}