package com.skilrock.paypr.paypr_app.data_class.response_data


import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.utility.*
import java.text.SimpleDateFormat
import java.util.*

data class MyTransactionResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("openingBalanceDate")
    @Expose
    val openingBalanceDate: String?,

    @SerializedName("txnList")
    @Expose
    val txnList: ArrayList<Txn?>?,

    @SerializedName("respMsg")
    @Expose
    var respMsg: String?,

    @SerializedName("txnTotalAmount")
    @Expose
    val txnTotalAmount: String?
) {
    data class Txn(

        @SerializedName("balance")
        @Expose
        val balance: Double?,

        @SerializedName("creditAmount")
        @Expose
        val creditAmount: Double?,

        @SerializedName("debitAmount")
        @Expose
        val debitAmount: Double?,

        @SerializedName("currency")
        @Expose
        val currency: String?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Int?,

        @SerializedName("openingBalance")
        @Expose
        val openingBalance: Double?,

        @SerializedName("particular")
        @Expose
        val particular: String?,

        @SerializedName("subwalletTxn")
        @Expose
        val subwalletTxn: String?,

        @SerializedName("transactionDate")
        @Expose
        val transactionDate: String?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: Int?,

        @SerializedName("txnAmount")
        @Expose
        val txnAmount: Double?,

        @SerializedName("txnType")
        @Expose
        val txnType: String?,

        @SerializedName("withdrawableBalance")
        @Expose
        val withdrawableBalance: Double?,

        @SerializedName("gameGroup")
        @Expose
        val gameGroup: String?
    ) {

        @SuppressLint("SimpleDateFormat")
        fun getDate() : String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val formatter = SimpleDateFormat("MMM d, yyyy")
                transactionDate?.let { tranDate ->
                    parser.parse(tranDate)?.let { date ->
                        formatter.format(date)
                    }
                } ?: "NA"
            } catch (e: Exception) {
                "NA"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime() : String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val formatter = SimpleDateFormat("hh:mm aa")
                transactionDate?.let { tranDate ->
                    parser.parse(tranDate)?.let { date ->
                        formatter.format(date)
                    }
                } ?: "NA"
            } catch (e: Exception) {
                "NA"
            }
        }

        fun getTxnTypeCaption() : String {
            return when (txnType) {
                "PLR_WAGER"             -> "Wager"
                "PLR_DEPOSIT"           -> "Deposit"
                "PLR_WITHDRAWAL"        -> "Withdrawal"
                "PLR_WINNING"           -> "Winning"
                "TRANSFER_IN"           -> "Cash In"
                "TRANSFER_OUT"          -> "Cash Out"
                "TRANSFER_OUT_REFUND"   -> "Refund"
                else -> "NA"
            }
        }

        fun getTxnId() : String {
            return "Txn Id: $transactionId"
        }

        fun getCrDrAmt() : String {
            if (creditAmount == null && debitAmount != null)
                return "Debit Amount: ${PlayerInfo.getCurrencyDisplayCode()} ${String.format("%.2f", debitAmount)}"
            if (debitAmount == null && creditAmount != null)
                return "Credit Amount: ${PlayerInfo.getCurrencyDisplayCode()} ${String.format("%.2f", creditAmount)}"

            return ""
        }

        fun getParticularStr() : String {
            var text = particular ?: ""
            text = text.replace(PLAYER, CONSUMER.toUpperCase(Locale.ROOT), ignoreCase = true)
            text = text.replace("User", CONSUMER.toUpperCase(Locale.ROOT), ignoreCase = true)
            text = text.replace(RETAILER, MERCHANT.toUpperCase(Locale.ROOT), ignoreCase = true)
            return text
        }

        fun getBalanceForReport() : String {
            return PlayerInfo.getCurrencyDisplayCode() + " " + String.format("%.2f", balance)
        }
    }
}