package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.PlayerInfo
import com.skilrock.paypr.utility.REQUEST_IP

data class FundTransferFromWalletRequestData(

    @SerializedName("amount")
    @Expose
    val amount: String?,

    @SerializedName("destinationAccount")
    @Expose
    val destinationAccount: String?,

    @SerializedName("merchantId")
    @Expose
    val merchantId: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("ipAddress")
    @Expose
    val ipAddress: String? = REQUEST_IP,

    @SerializedName("machineId")
    @Expose
    val machineId: String? = "123456",

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("txnType")
    @Expose
    val txnType: String? = "transfer_Out"

    /*@SerializedName("amount")
    @Expose
    val amount: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("ipAddress")
    @Expose
    val ipAddress: String?,

    @SerializedName("machineId")
    @Expose
    val machineId: String?,

    @SerializedName("merchantId")
    @Expose
    val merchantId: String?,

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("refTxnNo")
    @Expose
    val refTxnNo: String?,

    @SerializedName("txnType")
    @Expose
    val txnType: String? = "transfer_Out"*/
)