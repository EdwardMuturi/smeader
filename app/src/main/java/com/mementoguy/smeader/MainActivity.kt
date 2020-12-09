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
import androidx.lifecycle.ViewModelProvider

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val messageList = ArrayList<String>()

    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var smsViewModel:  SmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
        list.adapter = arrayAdapter

        smsViewModel= ViewModelProvider(this).get(SmsViewModel::class.java)
        requestSmsPermission()
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
        if (ContextCompat.checkSelfPermission(baseContext, "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            messageList.addAll(smsViewModel.readSms(contentResolver, "MPESA"))
            arrayAdapter.addAll(messageList)
        } else {
            val REQUEST_CODE_READ_SMS = 123
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_SMS"), REQUEST_CODE_READ_SMS)
        }
    }

    fun updateList(smsMessage: String) {
        arrayAdapter.insert(smsMessage, 0)
        arrayAdapter.notifyDataSetChanged()
    }

    companion object {
        lateinit var mainActivityInstance: MainActivity
    }
}