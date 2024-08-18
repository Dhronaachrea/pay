package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig

data class RegistrationValidationRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("emailid")
    @Expose
    val emailid: String?,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String?,

    @SerializedName("userName")
    @Expose
    val userName: String?
)