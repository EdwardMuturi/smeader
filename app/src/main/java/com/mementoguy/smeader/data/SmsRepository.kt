package com.mementoguy.smeader.data

import android.util.Log
import com.mementoguy.smeader.data.api.ServerApi
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

object SmsRepository {
    private val tag = SmsRepository::class.java.simpleName

    suspend fun sendPaymentBackUp(paymentBackUP: Map<String, String>) {
        try {

            val response = ServerApi.getServerApi.sendPaymentBackUp(paymentBackUP)
            if (response.isSuccessful && response.body() != null) {
                Log.e("Mpesa Fields Map", paymentBackUP.entries.toString())
            } else Log.e(tag, "sendPaymentBackup Request failed to send payment backup: ${response.message()}")

        } catch (e: Exception) {
            Log.e(tag, "sendPaymentBackup Request failed to send payment backup: ${e.localizedMessage}")
        }

    }

}