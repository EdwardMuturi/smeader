package com.mementoguy.smeader

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val messageList = ArrayList<String>()

    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
        list.adapter = arrayAdapter

        requestSmsPermission()

        sendMpesaReceiptToServer(messageList)
    }

    private fun sendMpesaReceiptToServer(messageList: ArrayList<String>) {
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
                    Log.e("Mpesa Field Data $index", paymentFields.entries.toString()) }
            }

        }

    }

    fun String.extractMpesaField(): List<String> {
        return this.split(" ")
    }

    override fun onStart() {
        super.onStart()

        mainActivityInstance = this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                baseContext,
                "android.permission.READ_SMS"
            ) == PackageManager.PERMISSION_GRANTED
        )
            readSms("MPESA")
        else {

            val REQUEST_CODE_READ_SMS = 123
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.READ_SMS"),
                REQUEST_CODE_READ_SMS
            )
        }
    }

    private fun readSms(senderId: String) {

        val smsColumns = arrayOf(Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.ADDRESS)
        val smsSelection = "${Telephony.TextBasedSmsColumns.ADDRESS} =?"
        val smsArgs = arrayOf(senderId)
        val smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), smsColumns, smsSelection, smsArgs, null)
        val indexBody = smsInboxCursor?.getColumnIndex("body")

        if (!smsInboxCursor!!.moveToFirst()) return
        arrayAdapter.clear()

        do {
            val smsMpesaSuccess = filterMpesaSuccessSms(smsInboxCursor.getString(indexBody!!))
            if (smsMpesaSuccess.isNotBlank())
                arrayAdapter.add(smsMpesaSuccess)

        } while (smsInboxCursor.moveToNext())
    }

    fun updateList(smsMessage: String) {
        arrayAdapter.insert(smsMessage, 0)
        arrayAdapter.notifyDataSetChanged()
    }

    fun filterMpesaSuccessSms(smsBody: String): String {
        val smsFirstLineSubstring = smsBody.split(".").first()
        var smsMpesaSuccess = ""

        if (smsFirstLineSubstring.contains("Confirmed"))
            smsMpesaSuccess = smsBody

        return smsMpesaSuccess
    }


    companion object {
        lateinit var mainActivityInstance: MainActivity
    }
}