package com.skilrock.paypr.utility


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QrCodeData(

    @SerializedName("merchantId")
    @Expose
    var merchantId: String,

    @SerializedName("destinationAccount")
    @Expose
    var destinationAccount: String,

    @SerializedName("amount")
    @Expose
    var amount: String = "",

    @SerializedName("name")
    @Expose
    var name: String = ""
)