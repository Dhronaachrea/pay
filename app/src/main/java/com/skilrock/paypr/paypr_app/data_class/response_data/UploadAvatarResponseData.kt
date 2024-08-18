package com.skilrock.paypr.paypr_app.data_class.response_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UploadAvatarResponseData(
    @SerializedName("avatarPath")
    @Expose
    val avatarPath: String? = "",

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    var showToast: Boolean = true
)