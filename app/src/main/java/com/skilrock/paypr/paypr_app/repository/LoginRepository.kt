package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.LoginRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import retrofit2.Response

class LoginRepository {

    suspend fun performLogin(loginRequestData: LoginRequestData) : Response<LoginResponseData> {
        return RetrofitNetworking.create().callLoginApi(loginRequestData)
    }

}