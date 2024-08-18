package com.skilrock.paypr.paypr_app.data_class.request_data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.utility.PlayerInfo

data class UpdateProfileRequestData(

    @SerializedName("firstName")
    @Expose
    val firstName: String?,

    @SerializedName("lastName")
    @Expose
    val lastName: String?,

    @SerializedName("emailId")
    @Expose
    val emailId: String?,

    @SerializedName("addressLine1")
    @Expose
    val addressLine1: String? = "",

    @SerializedName("city")
    @Expose
    val city: String? = "",

    @SerializedName("dob")
    @Expose
    val dob: String? = "",

    @SerializedName("stateCode")
    @Expose
    val stateCode: String? = "",

    @SerializedName("gender")
    @Expose
    val gender: String? = "",

    @SerializedName("pinCode")
    @Expose
    val pinCode: String? = "",

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String? = PlayerInfo.getPlayerMobileNumber(),

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)