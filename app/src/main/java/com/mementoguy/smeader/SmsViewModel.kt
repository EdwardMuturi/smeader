package com.mementoguy.smeader

import android.content.ContentResolver
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import androidx.lifecycle.ViewModel

class SmsViewModel : ViewModel() {

    fun readSms(contentResolver: ContentResolver, senderId: String): List<String> {

        val smsMessages : MutableList<String> = ArrayList()

        val smsColumns = arrayOf(Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.ADDRESS)
        val smsSelection = "${Telephony.TextBasedSmsColumns.ADDRESS} =?"
        val smsArgs = arrayOf(senderId)
        val smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), smsColumns, smsSelection, smsArgs, null)
        val indexBody = smsInboxCursor?.getColumnIndex("body")

        if (!smsInboxCursor!!.moveToFirst())
            smsMessages.clear()

        do {
            val smsMpesaSuccess = filterMpesaSuccessSms(smsInboxCursor.getString(indexBody!!))
            if (smsMpesaSuccess.isNotBlank())
                smsMessages.add(smsMpesaSuccess)

        } while (smsInboxCursor.moveToNext())

        sendMpesaReceiptToServer(smsMessages as ArrayList<String>)

        return smsMessages
    }

    fun filterMpesaSuccessSms(smsBody: String): String {

    val smsFirstLineSubstring = smsBody.split(".").first()
        var smsMpesaSuccess = ""

        if (smsFirstLineSubstring.contains("Confirmed"))
            smsMpesaSuccess = smsBody

        return smsMpesaSuccess
    }

    fun sendMpesaReceiptToServer(messageList: ArrayList<String>) {
        messageList.forEach{ message ->
            val fieldsArray = message.split(".")
            fieldsArray.extractMpesaFields()
        }
    }

    fun List<String>.extractMpesaFields() {

        var mpesaReceipt: String? = null
        var date: String? = null
        var time: String? = null
        var amount: String? = null
        var am_pm: String? = null
        var customer: String? = null
        var accountNumber: String? = null

        this.forEachIndexed { index, message ->

            when (index) {
                0 -> {
                    mpesaReceipt = message.extractMpesaField().first()
                }

                1 -> {
                    val dateArray = message.extractMpesaField()

                    time = dateArray[4]
                    am_pm = dateArray[5]
                    date = "${dateArray[2]} $time $am_pm"
                    amount = dateArray[6].removePrefix("Ksh")

                }

                2 -> {
                    val customerArray= message.extractMpesaField()
                    customer = "+${customerArray.find { it.startsWith("254", true) }}"
                }

                3 -> {
                    val accountArray = message.extractMpesaField()
                    accountNumber = "${accountArray[4]} ${accountArray[5]}"
                }
                4->{ val paymentFields= mapOf("transactionReceipt" to mpesaReceipt, "paidOn" to date, "amount" to amount, "accountNumber" to accountNumber, "customer" to customer)
                    Log.e("Mpesa Fields Map", paymentFields.entries.toString()) }
            }

        }

    }

    fun String.extractMpesaField(): List<String> {
        return this.split(" ")
    }
}