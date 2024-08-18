package com.skilrock.paypr.network

import com.skilrock.paypr.paypr_app.data_class.request_data.*
import com.skilrock.paypr.paypr_app.data_class.response_data.*
import com.skilrock.paypr.utility.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiCallInterface {

    @Headers(CONTENT_TYPE)
    @POST(VERSION_URL)
    suspend fun callVersionApi(
        @Body versionRequestData: VersionRequestData
    ): Response<VersionResponseData>

    @Headers(CONTENT_TYPE)
    @POST(LOGIN_URL)
    suspend fun callLoginApi(
        @Body loginRequestData: LoginRequestData
    ): Response<LoginResponseData>

    @Headers(CONTENT_TYPE)
    @POST(REGISTRATION_URL)
    suspend fun callRegistrationApi(
        @Body registrationRequestData: RegistrationRequestData
    ): Response<RegistrationResponseData>

    @Headers(CONTENT_TYPE)
    @POST(REGISTRATION_OTP_URL)
    suspend fun callRegistrationOtpApi(
        @Body registrationOtpRequestData: RegistrationOtpRequestData
    ): Response<RegistrationOtpResponseData>

    @Headers(CONTENT_TYPE)
    @POST(REGISTRATION_VALIDATION_URL)
    suspend fun callRegistrationValidationApi(
        @Body registrationValidationRequestData: RegistrationValidationRequestData
    ): Response<RegistrationValidationResponseData>

    @Headers(CONTENT_TYPE)
    @POST(FORGOT_PASSWORD_URL)
    suspend fun callForgotPasswordOtpApi(
        @Body forgotPasswordRequestData: ForgotPasswordRequestData
    ): Response<ForgotPasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(RESET_PASSWORD_URL)
    suspend fun callForgotPasswordResetApi(
        @Body forgotPasswordRequestData: ForgotPasswordRequestData
    ): Response<ForgotPasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(LOGOUT_URL)
    suspend fun callLogoutApi(
        @Body logoutRequestData: LogoutRequestData
    ): Response<LogoutResponseData>

    @Headers(CONTENT_TYPE)
    @POST(UPDATE_BALANCE_URL)
    suspend fun callUpdateBalanceApi(
        @Body balanceRequestData: BalanceRequestData
    ): Response<BalanceResponseData>

    @Headers(CONTENT_TYPE)
    @POST(MY_TRANSACTIONS_URL)
    suspend fun callMyTransactionsApi(
        @Body myTransactionRequestData: MyTransactionRequestData
    ): Response<MyTransactionResponseData>

    @Headers(CONTENT_TYPE)
    @POST(CHANGE_PASSWORD_URL)
    suspend fun callChangePasswordApi(
        @Body changePasswordRequestData: ChangePasswordRequestData
    ): Response<ChangePasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(UPDATE_PROFILE_URL)
    suspend fun callProfileUpdateApi(
        @Body updateProfileRequestData: UpdateProfileRequestData
    ): Response<UpdateProfileResponseData>

    @Multipart
    @POST(UPLOAD_AVATAR_URL)
    suspend fun callProfilePicApi(
        @PartMap paramsMap: HashMap<String, RequestBody>,
        @Part imageFile: MultipartBody.Part
    ) : Response<UploadAvatarResponseData>

    @Headers(CONTENT_TYPE)
    @POST(FUND_TRANSFER_URL)
    suspend fun callFundTransferFromWalletApi(
        @Body fundTransferFromWalletRequestData: FundTransferFromWalletRequestData
    ): Response<FundTransferFromWalletResponseData>

    @Headers(CONTENT_TYPE)
    @POST(LAST_LOGIN_URL)
    suspend fun callLastLoginApi(
        @Body lastLoginRequestData: LastLoginRequestData
    ): Response<LastLoginResponseData>

    @GET(MERCHANT_LOCATION_URL)
    suspend fun callRetailerLocationApi(
        @Header("Authorization") authorization: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double
    ): Response<MerchantLocationResponseData>
}