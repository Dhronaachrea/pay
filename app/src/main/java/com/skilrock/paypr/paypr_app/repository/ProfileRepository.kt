package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.*
import com.skilrock.paypr.paypr_app.data_class.response_data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProfileRepository {

    suspend fun callLogoutApi(logoutRequestData: LogoutRequestData) : Response<LogoutResponseData> {
        return RetrofitNetworking.create().callLogoutApi(logoutRequestData)
    }

    suspend fun callBalanceApi(balanceRequestData: BalanceRequestData) : Response<BalanceResponseData> {
        return RetrofitNetworking.create().callUpdateBalanceApi(balanceRequestData)
    }

    suspend fun callTransactionApi(myTransactionRequestData: MyTransactionRequestData) : Response<MyTransactionResponseData> {
        return RetrofitNetworking.create().callMyTransactionsApi(myTransactionRequestData)
    }

    suspend fun callChangePassword(changePasswordRequestData: ChangePasswordRequestData): Response<ChangePasswordResponseData> {
        return RetrofitNetworking.create().callChangePasswordApi(changePasswordRequestData)
    }

    suspend fun callProfileUpdateApi(updateProfileRequestData: UpdateProfileRequestData): Response<UpdateProfileResponseData> {
        return RetrofitNetworking.create().callProfileUpdateApi(updateProfileRequestData)
    }

    suspend fun callUpdateAvatarApi(requestParamsMap: HashMap<String, RequestBody>, multipartFile: MultipartBody.Part) : Response<UploadAvatarResponseData>{
        return RetrofitNetworking.create().callProfilePicApi(requestParamsMap, multipartFile)
    }

}