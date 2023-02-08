package com.example.myretrofittestapplication.feature

import android.util.Log
import com.example.myretrofittestapplication.NetworkConfiguration
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PointFormFeatureDataSource {

    fun fetchOne(callback: (Feature<PointForm>) -> Unit) {
        val api = getApi()
        api.getOne().enqueue(object : Callback<Feature<PointForm>> {

            override fun onResponse(call: Call<Feature<PointForm>>, response: Response<Feature<PointForm>>) {
                if (response.isSuccessful) {
                    val feature: Feature<PointForm>? = response.body()
                    feature?.also {
                        Log.i("DATA", "Fact: ${it.id} ${it.geometry.javaClass.typeName} ${it.properties.name}")
                        callback.invoke(it)
                    }
                } else {
                    Log.e("ERROR", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Feature<PointForm>>, t: Throwable) {
                Log.e("ERROR", "Error: ${t.stackTrace}")
            }
        })
    }

    companion object {

        fun getApi(): PointFormFeatureApi {
            val gson: Gson = GsonBuilder()
                .setLenient()
                .create();

            return Retrofit.Builder()
                .baseUrl(NetworkConfiguration.MYSERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(PointFormFeatureApi::class.java)
        }
    }
}

