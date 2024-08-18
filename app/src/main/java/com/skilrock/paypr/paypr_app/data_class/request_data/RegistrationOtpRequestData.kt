package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.REQUEST_IP

data class RegistrationOtpRequestData(

    @SerializedName("currencyId")
    @Expose
    val currencyId: Int = BuildConfig.CURRENCY_ID,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("emailId")
    @Expose
    val emailId: String?,

    @SerializedName("firstName")
    @Expose
    val firstName: String?,

    @SerializedName("lastName")
    @Expose
    val lastName: String?,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: Long?,

    @SerializedName("password")
    @Expose
    val password: String?,

    @SerializedName("referCode")
    @Expose
    val referCode: String?,

    @SerializedName("registrationType")
    @Expose
    val registrationType: String? = "FULL",

    @SerializedName("requestIp")
    @Expose
    val requestIp: String? = REQUEST_IP,

    @SerializedName("userName")
    @Expose
    val userName: String?
)