package com.skilrock.paypr.paypr_app.data_class.response_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BalanceResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("respMsg")
    @Expose
    var respMsg: String?,

    @SerializedName("wallet")
    @Expose
    val wallet: Wallet?,

    var showToast: Boolean = true
) {
    data class Wallet(

        @SerializedName("bonusBalance")
        @Expose
        val bonusBalance: Double?,

        @SerializedName("cashBalance")
        @Expose
        val cashBalance: Double?,

        @SerializedName("currency")
        @Expose
        val currency: String?,

        @SerializedName("depositBalance")
        @Expose
        val depositBalance: Double?,

        @SerializedName("practiceBalance")
        @Expose
        val practiceBalance: Double?,

        @SerializedName("preferredWallet")
        @Expose
        val preferredWallet: String?,

        @SerializedName("totalBalance")
        @Expose
        val totalBalance: Double?,

        @SerializedName("totalDepositBalance")
        @Expose
        val totalDepositBalance: Double?,

        @SerializedName("totalWithdrawableBalance")
        @Expose
        val totalWithdrawableBalance: Double?,

        @SerializedName("winningBalance")
        @Expose
        val winningBalance: Double?,

        @SerializedName("withdrawableBal")
        @Expose
        val withdrawableBal: Double?,

        @SerializedName("totalWinningBalance")
        @Expose
        val totalWinningBalance: Double?
    )
}