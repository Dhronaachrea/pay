package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.DEVICE_TYPE
import com.skilrock.paypr.utility.LOGIN_DEVICE
import com.skilrock.paypr.utility.REQUEST_IP

data class RegistrationRequestData(

    @SerializedName("countryCode")
    @Expose
    val countryCode: String? = BuildConfig.COUNTRY_CODE,

    @SerializedName("currencyId")
    @Expose
    val currencyId: Int? = BuildConfig.CURRENCY_ID,

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("firstName")
    @Expose
    val firstName: String?,

    @SerializedName("lastName")
    @Expose
    val lastName: String?,

    @SerializedName("otp")
    @Expose
    val otp: String?,

    @SerializedName("emailId")
    @Expose
    val emailId: String?,

    @SerializedName("loginDevice")
    @Expose
    val loginDevice: String? = LOGIN_DEVICE,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String?,

    @SerializedName("password")
    @Expose
    val password: String?,

    @SerializedName("registrationType")
    @Expose
    val registrationType: String? = "FULL",

    @SerializedName("requestIp")
    @Expose
    val requestIp: String? = REQUEST_IP,

    @SerializedName("referCode")
    @Expose
    val referCode: String?,

    @SerializedName("userAgent")
    @Expose
    val userAgent: String? = System.getProperty("http.agent"),

    @SerializedName("userName")
    @Expose
    val userName: String?
)