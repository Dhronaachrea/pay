package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig

data class VersionRequestData(

    @SerializedName("appType")
    @Expose
    val appType: String? = "CASH",

    @SerializedName("currAppVer")
    @Expose
    val currAppVer: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("os")
    @Expose
    val os: String? = "ANDROID",

    @SerializedName("playerToken")
    @Expose
    val playerToken: String?
)