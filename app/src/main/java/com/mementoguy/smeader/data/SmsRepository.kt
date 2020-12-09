package com.mementoguy.smeader.data

import android.util.Log
import com.mementoguy.smeader.data.api.ServerApi
import retrofit2.Call
import retrofit2.Response

object SmsRepository {
    val tag= SmsRepository::class.java.simpleName
    fun sendPaymentBackUp(paymentBackUP: Map<String, String>) {

        ServerApi.getServerApi.sendPaymentBackUp(paymentBackUP)
            .enqueue(object : retrofit2.Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.e("Mpesa Fields Map", paymentBackUP.entries.toString())
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Log.e(tag, "sendPaymentBackup Request failed to send payment backup: ${t.localizedMessage}")
                }

            })
    }
}