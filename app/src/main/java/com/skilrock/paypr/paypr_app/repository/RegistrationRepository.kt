package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.LoginRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationOtpRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationRequestData
import com.skilrock.paypr.paypr_app.data_class.request_data.RegistrationValidationRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationOtpResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationValidationResponseData
import retrofit2.Response

class RegistrationRepository {

    suspend fun registerPlayer(registrationRequestData: RegistrationRequestData) : Response<RegistrationResponseData> {
        return RetrofitNetworking.create().callRegistrationApi(registrationRequestData)
    }

    suspend fun registrationOtp(registrationOtpRequestData: RegistrationOtpRequestData) : Response<RegistrationOtpResponseData> {
        return RetrofitNetworking.create().callRegistrationOtpApi(registrationOtpRequestData)
    }

    suspend fun registrationValidation(registrationValidationRequestData: RegistrationValidationRequestData) : Response<RegistrationValidationResponseData> {
        return RetrofitNetworking.create().callRegistrationValidationApi(registrationValidationRequestData)
    }

    suspend fun performLogin(loginRequestData: LoginRequestData) : Response<LoginResponseData> {
        return RetrofitNetworking.create().callLoginApi(loginRequestData)
    }
}