package com.skilrock.paypr.paypr_app.data_class.response_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FundTransferFromWalletResponseData(

    @SerializedName("amount")
    @Expose
    val amount: Double?,

    @SerializedName("destinationAccount")
    @Expose
    val destinationAccount: String?,

    @SerializedName("errorMsg")
    @Expose
    var errorMsg: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("playerId")
    @Expose
    val playerId: Int?,

    @SerializedName("playerName")
    @Expose
    val playerName: String?,

    @SerializedName("totalBal")
    @Expose
    val totalBal: String?,

    @SerializedName("txnId")
    @Expose
    val txnId: Int?
)