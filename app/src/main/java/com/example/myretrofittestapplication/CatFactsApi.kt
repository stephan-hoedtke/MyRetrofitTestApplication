package com.example.myretrofittestapplication

import retrofit2.Call
import retrofit2.http.GET

interface CatFactsApi {
    @GET(NetworkConfiguration.CATFACTS_FACT_ENDPOINT)
    fun getOne(): Call<CatFact>
}


