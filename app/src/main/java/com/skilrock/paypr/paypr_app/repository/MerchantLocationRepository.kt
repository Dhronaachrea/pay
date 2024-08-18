package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData
import retrofit2.Response

class MerchantRepository {

    suspend fun callRetailerLocationApi(authorization: String, latitude: Double, longitude: Double, radius: Double) : Response<MerchantLocationResponseData> {
        return RetrofitNetworking.create().callRetailerLocationApi("Bearer $authorization", latitude, longitude, radius)
    }

}