package com.skilrock.paypr.paypr_app.repository

import com.skilrock.paypr.network.RetrofitNetworking
import com.skilrock.paypr.paypr_app.data_class.request_data.VersionRequestData
import com.skilrock.paypr.paypr_app.data_class.response_data.VersionResponseData
import retrofit2.Response

class SplashRepository {

    suspend fun callVersionApi(versionRequestData: VersionRequestData) : Response<VersionResponseData> {
        return RetrofitNetworking.create().callVersionApi(versionRequestData)
    }

}