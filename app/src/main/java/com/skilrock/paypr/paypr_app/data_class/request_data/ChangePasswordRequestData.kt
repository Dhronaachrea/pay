package com.skilrock.paypr.paypr_app.data_class.request_data

import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.PlayerInfo

data class ChangePasswordRequestData(
    val domainName: String? = BuildConfig.DOMAIN_NAME,
    val newPassword: String? = "",
    val oldPassword: String? = "",
    val playerId: Int? = PlayerInfo.getPlayerId(),
    val playerToken: String? = PlayerInfo.getPlayerToken()
)