package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.PlayerInfo

data class LastLoginRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)