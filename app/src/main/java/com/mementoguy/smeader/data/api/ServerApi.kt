package com.mementoguy.smeader.data.api

import com.mementoguy.smeader.BuildConfig
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ServerApi {
    @POST("/sacco/backup/payment")
    suspend fun sendPaymentBackUp(@Body paymentBackUp : Map<String, String>) : Response<List<String>>

    companion object{
        val getServerApi : ServerApi
        get() {
            val retrofit= Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

           return retrofit.create(ServerApi::class.java)
        }
    }
}

private const val BASE_URL= BuildConfig.SERVER_URL