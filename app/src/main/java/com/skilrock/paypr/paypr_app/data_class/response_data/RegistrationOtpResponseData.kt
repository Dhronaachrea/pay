package com.skilrock.paypr.paypr_app.data_class.response_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegistrationOtpResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("respMsg")
    @Expose
    var respMsg: String?
)