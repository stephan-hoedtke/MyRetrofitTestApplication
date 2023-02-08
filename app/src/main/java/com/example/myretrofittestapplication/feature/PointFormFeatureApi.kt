package com.example.myretrofittestapplication.feature

import com.example.myretrofittestapplication.NetworkConfiguration
import retrofit2.Call
import retrofit2.http.GET

interface PointFormFeatureApi {
    @GET(NetworkConfiguration.MYSERVER_POINT_DETAILS_ENDPOINT)
    fun getOne(): Call<Feature<PointForm>>
}


