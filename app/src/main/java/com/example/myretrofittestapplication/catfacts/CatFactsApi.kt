package com.example.myretrofittestapplication.catfacts

import com.example.myretrofittestapplication.NetworkConfiguration
import retrofit2.Call
import retrofit2.http.GET

interface CatFactsApi {
    @GET(NetworkConfiguration.CATFACTS_FACT_ENDPOINT)
    fun getOne(): Call<CatFact>
}


