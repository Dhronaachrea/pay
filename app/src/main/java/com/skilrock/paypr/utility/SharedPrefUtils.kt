package com.skilrock.paypr.utility

import android.content.Context

object SharedPrefUtils {

    private const val USER_PREF         = "PAYPR"
    private const val FCM_TOKEN         = "FcmTokenPaypr"
    private const val LOGGED_IN_USERS   = "RefTxnNo"
    const val PLAYER_TOKEN              = "playerToken"
    const val PLAYER_USER_NAME          = "playerUserName"
    const val PLAYER_ID                 = "playerId"
    const val PLAYER_MOBILE_NUMBER      = "playerMobileNo"
    const val PLAYER_FIRST_NAME         = "playerFirstName"
    const val PLAYER_LAST_NAME          = "playerLastName"
    const val PLAYER_DATA               = "playerData"
    private const val USER_LOGGED_IN    = "playerLastName"

    fun getString(context: Context, key: String): String {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getString(key, "") ?: ""
    }

    fun setString(context: Context, key: String, value: String) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }

    fun setInt(context: Context, key: String, value: Int) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun clearAppSharedPref(context: Context) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun putLoggedInUsers(context: Context, value: String) {
        var str = context.getSharedPreferences(LOGGED_IN_USERS, Context.MODE_PRIVATE).getString(USER_LOGGED_IN, "") ?: ""
        if (str.isBlank())
            context.getSharedPreferences(LOGGED_IN_USERS, Context.MODE_PRIVATE).edit().putString(USER_LOGGED_IN, value).apply()
        else {
            str = "$str###$value"
            context.getSharedPreferences(LOGGED_IN_USERS, Context.MODE_PRIVATE).edit().putString(USER_LOGGED_IN, str).apply()
        }
    }

    fun setFcmToken(context: Context, value: String) {
        context.getSharedPreferences(FCM_TOKEN, Context.MODE_PRIVATE).edit().putString("token", value).apply()
    }

    fun getFcmToken(context: Context): String {
        return context.getSharedPreferences(FCM_TOKEN, Context.MODE_PRIVATE).getString("token", "") ?: ""
    }

}