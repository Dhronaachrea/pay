package com.skilrock.paypr.paypr_app.data_class.response_data


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantLocationResponseData(

    @SerializedName("responseCode")
    @Expose
    val responseCode: Int?,

    @SerializedName("responseData")
    @Expose
    val responseData: ResponseData?,

    @SerializedName("responseMessage")
    @Expose
    var responseMessage: String?
) : Parcelable {
    @Parcelize
    data class ResponseData(

        @SerializedName("data")
        @Expose
        val data: ArrayList<Data?>?,

        @SerializedName("message")
        @Expose
        var message: String?,

        @SerializedName("statusCode")
        @Expose
        val statusCode: Int?
    ) : Parcelable {
        @Parcelize
        data class Data(

            @SerializedName("addressOne")
            @Expose
            val addressOne: String?,

            @SerializedName("addressTwo")
            @Expose
            val addressTwo: String?,

            @SerializedName("city")
            @Expose
            val city: String?,

            @SerializedName("country")
            @Expose
            val country: String?,

            @SerializedName("distance")
            @Expose
            val distance: Double?,

            @SerializedName("latitudes")
            @Expose
            val latitudes: String?,

            @SerializedName("longitudes")
            @Expose
            val longitudes: String?,

            @SerializedName("mobileCode")
            @Expose
            val mobileCode: String?,

            @SerializedName("mobileNumber")
            @Expose
            val mobileNumber: String?,

            @SerializedName("orgId")
            @Expose
            val orgId: Int?,

            @SerializedName("orgName")
            @Expose
            val orgName: String?,

            @SerializedName("state")
            @Expose
            val state: String?,

            @SerializedName("userId")
            @Expose
            val userId: Int?,

            @SerializedName("username")
            @Expose
            val username: String?
        ) : Parcelable {

            fun getAddress() : String {
                return "$addressOne, $addressTwo, $city${if (state.isNullOrBlank()) "" else ", $state"}${if (country.isNullOrBlank()) "" else ", $country"}"
            }

            fun getMerchantId() : String {
                return "Merchant Id: $orgId"
            }

            fun getMobileNumberWithCode() : String {
                return "Mobile: $mobileCode $mobileNumber"
            }

        }
    }
}