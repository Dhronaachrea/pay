package com.skilrock.paypr.paypr_app.data_class.response_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LastLoginResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("loginDetailList")
    @Expose
    val loginDetailList: ArrayList<LoginDetail?>?,

    @SerializedName("formattedDate")
    @Expose
    val formattedDate: String?,

    @SerializedName("respMsg")
    @Expose
    var respMsg: String?
) {
    data class LoginDetail(

        @SerializedName("appVersion")
        @Expose
        val appVersion: String?,

        @SerializedName("browser")
        @Expose
        val browser: String?,

        @SerializedName("city")
        @Expose
        val city: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("device")
        @Expose
        val device: String?,

        @SerializedName("domainId")
        @Expose
        val domainId: String?,

        @SerializedName("id")
        @Expose
        val id: String?,

        @SerializedName("ipAddress")
        @Expose
        val ipAddress: String?,

        @SerializedName("loginDate")
        @Expose
        val loginDate: String?,

        @SerializedName("loginDevice")
        @Expose
        val loginDevice: String?,

        @SerializedName("logoutDate")
        @Expose
        val logoutDate: String?,

        @SerializedName("os")
        @Expose
        val os: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: String?,

        @SerializedName("status")
        @Expose
        val status: String?,

        @SerializedName("userAgent")
        @Expose
        val userAgent: String?
    )
}