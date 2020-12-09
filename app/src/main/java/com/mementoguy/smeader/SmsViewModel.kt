package com.mementoguy.smeader

import android.net.Uri
import android.provider.Telephony
import androidx.lifecycle.ViewModel

class SmsViewModel : ViewModel() {

//    private fun readSms(senderId: String) {
//
//        val smsColumns = arrayOf(Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.ADDRESS)
//        val smsSelection = "${Telephony.TextBasedSmsColumns.ADDRESS} =?"
//        val smsArgs = arrayOf(senderId)
//
//        val smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), smsColumns, smsSelection, smsArgs, null)
//        val indexBody = smsInboxCursor?.getColumnIndex("body")
//
//        if (!smsInboxCursor!!.moveToFirst()) return
//        arrayAdapter.clear()
//
//        do {
//            val smsMpesaSuccess = filterMpesaSuccessSms(smsInboxCursor.getString(indexBody!!))
//            if (smsMpesaSuccess.isNotBlank())
//                arrayAdapter.add(smsMpesaSuccess)
//
//        } while (smsInboxCursor.moveToNext())
//    }
//
//    fun updateList(smsMessage: String) {
//        arrayAdapter.insert(smsMessage, 0)
//        arrayAdapter.notifyDataSetChanged()
//    }
//
//    fun filterMpesaSuccessSms(smsBody: String): String {
////        each successfull transaction has 'Confirmed' keyword before the first fullstop `.`
//        val smsFirstLineSubstring = smsBody.split(".").first()
//        var smsMpesaSuccess = ""
//
//        if (smsFirstLineSubstring.contains("Confirmed"))
//            smsMpesaSuccess = smsBody
//
//        return smsMpesaSuccess
//    }
}