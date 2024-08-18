package com.skilrock.paypr.utility

import android.content.Context
import com.google.gson.Gson
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import java.util.*

object PlayerInfo {

    private var loginData: LoginResponseData? = null

    fun setLoginData(loginResponse: LoginResponseData) {
        loginData = loginResponse
    }

    fun destroy() {
        loginData = null
    }

    fun setLoginData(context: Context, loginResponse: LoginResponseData) {
        loginData = loginResponse
        SharedPrefUtils.setInt(context, SharedPrefUtils.PLAYER_ID, loginResponse.playerLoginInfo?.playerId ?: 0)
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_TOKEN, loginResponse.playerToken ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_USER_NAME, loginResponse.playerLoginInfo?.userName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_MOBILE_NUMBER, loginResponse.playerLoginInfo?.mobileNo ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_FIRST_NAME, loginResponse.playerLoginInfo?.firstName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_LAST_NAME, loginResponse.playerLoginInfo?.lastName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginResponse))
    }

    fun setBalance(context: Context, wallet: BalanceResponseData.Wallet) {
        loginData?.playerLoginInfo?.walletBean?.let {
            it.bonusBalance             = wallet.bonusBalance
            it.cashBalance              = wallet.cashBalance
            it.currency                 = wallet.currency
            it.depositBalance           = wallet.depositBalance
            it.practiceBalance          = wallet.practiceBalance
            it.preferredWallet          = wallet.preferredWallet
            it.totalBalance             = wallet.totalBalance
            it.totalDepositBalance      = wallet.totalDepositBalance
            it.totalWithdrawableBalance = wallet.totalWithdrawableBalance
            it.winningBalance           = wallet.winningBalance
            it.withdrawableBal          = wallet.withdrawableBal
            it.totalWinningBalance      = wallet.totalWinningBalance
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setFirstNameLastNameEmail(context: Context, firstName: String, lastName: String, email: String) {
        loginData?.playerLoginInfo?.let {
            it.firstName    = firstName
            it.lastName     = lastName
            it.emailId      = email
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setProfileImage(path: String) {
        loginData?.playerLoginInfo?.avatarPath = path
    }

    fun getPlayerId() : Int = loginData?.playerLoginInfo?.playerId ?: 0
    fun getPlayerToken() : String = loginData?.playerToken ?: ""
    fun getPlayerUserName() : String = loginData?.playerLoginInfo?.userName ?: ""
    fun getPlayerMobileNumber() : String = loginData?.playerLoginInfo?.mobileNo ?: ""
    fun getPlayerFirstName() : String = loginData?.playerLoginInfo?.firstName?.capitalize(Locale.ROOT) ?: ""
    fun getPlayerEmailId() : String = loginData?.playerLoginInfo?.emailId ?: ""
    fun getPlayerLastName() : String = loginData?.playerLoginInfo?.lastName?.capitalize(Locale.ROOT) ?: ""
    fun getPlayerTotalBalance() : String = loginData?.playerLoginInfo?.walletBean?.totalBalance?.toString() ?: "0"
    fun getCurrencyDisplayCode() : String = loginData?.playerLoginInfo?.walletBean?.currencyDisplayCode ?: BuildConfig.CURRENCY_DISPLAY_CODE
    fun getProfilePicPath() : String = loginData?.playerLoginInfo?.commonContentPath + loginData?.playerLoginInfo?.avatarPath.toString()
    fun getPlayerInfo() : LoginResponseData? = loginData
}