package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.ForgotPasswordRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.ForgotPasswordResponseData
import retrofit2.Response

class ForgotPasswordRepository {

    suspend fun sendOtp(forgotPasswordRequestData: ForgotPasswordRequestData) : Response<ForgotPasswordResponseData> {
        return RetrofitNetworking.create().callForgotPasswordOtpApi(forgotPasswordRequestData)
    }

    suspend fun resetPassword(forgotPasswordRequestData: ForgotPasswordRequestData) : Response<ForgotPasswordResponseData> {
        return RetrofitNetworking.create().callForgotPasswordResetApi(forgotPasswordRequestData)
    }

}