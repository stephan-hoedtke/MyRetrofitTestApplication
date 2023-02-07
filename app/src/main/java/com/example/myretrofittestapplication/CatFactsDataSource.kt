package com.example.myretrofittestapplication

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatFactsDataSource {

    fun fetchOne(callback: (CatFact) -> Unit) {
        val api = getApi()
        api.getOne().enqueue(object : Callback<CatFact> {

            override fun onResponse(call: Call<CatFact>, response: Response<CatFact>) {
                if (response.isSuccessful) {
                    val fact: CatFact? = response.body()
                    fact?.also {
                        Log.i("DATA", "Fact: ${it.fact} ${it.length}")
                        callback.invoke(it)
                    }
                } else {
                    Log.e("ERROR", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<CatFact>, t: Throwable) {
                Log.e("ERROR", "Error: ${t.stackTrace}")
            }
        })
    }

    companion object {

        fun getApi(): CatFactsApi {
            val gson: Gson = GsonBuilder()
                .setLenient()
                .create();

            return Retrofit.Builder()
                .baseUrl(NetworkConfiguration.CATFACTS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(CatFactsApi::class.java)
        }
    }
}

