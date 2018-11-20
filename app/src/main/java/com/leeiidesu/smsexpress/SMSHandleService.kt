package com.leeiidesu.smsexpress

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.leeiidesu.smsexpress.model.SMS
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import java.util.regex.Pattern

class SMSHandleService : Service() {
    private val NAME = "sms-sp"
    private val mBinder = SMSBinder(this)
    private val keywords = "菜鸟驿站"
    private val argKeywords = "%$keywords%"
    private val argKeywordsJD = "%请凭提货码%火速来取%"
    private var onNewMessageListener: OnNewMessageListener? = null

    private var boxStore: BoxStore? = null
    private val uri = Uri.parse("content://sms/")


    private val smSObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)

            loadNewSMS()
        }
    }

    override fun onCreate() {
        super.onCreate()
        boxStore = (application as App).boxStore
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    fun loadNewSMS(): Boolean {
        val sharedPreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val last = sharedPreferences.getLong("last_time", 0)

        val projection = arrayOf("address", "person", "date", "type", "body", "subject")


        val query = contentResolver.query(uri,
                projection,
                "date>? AND (body LIKE ? OR body LIKE ?)",
                arrayOf("$last", argKeywords, argKeywordsJD),
                "date DESC")


        if (query != null && query.count > 0) {
            val smsList = arrayListOf<SMS>()
            while (query.moveToNext()) {
                val address = query.getString(query.getColumnIndex("address"))
                val person = query.getString(query.getColumnIndex("person"))
                val date = query.getLong(query.getColumnIndex("date"))
                val type = query.getInt(query.getColumnIndex("type"))
                val body = query.getString(query.getColumnIndex("body"))
                val subject = query.getString(query.getColumnIndex("subject"))

                val code = findGetCode(body)
                val expressName = findExpressName(body)
                val sms = SMS(0, address, person, date, type, body, subject, code, expressName)
                smsList.add(sms)
            }
            query.close()

            val boxFor = boxStore!!.boxFor(SMS::class)
            boxFor.put(smsList)

            sharedPreferences.edit()
                    .putLong("last_time", System.currentTimeMillis())
                    .apply()

            onNewMessageListener?.onNewMessage()
            return true
        }

        query?.close()
        return false
    }

    private fun findExpressName(body: String?): String? {
        if (body == null)
            return null
        if (body.contains("京东"))
            return "京东物流"

        val compile = Pattern.compile("您的[\\u4e00-\\u9fa5]+包裹").matcher(body)

        if (compile.find()) {
            val group = compile.group()
            return group.substring(2, group.length - 2)
        }
        return null
    }

    private fun findGetCode(body: String?): String? {
        val p = Pattern.compile("[\\d-]{6,}")
        val matcher = p.matcher(body)
        if (matcher.find()) {
            return matcher.group()
        }

        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        contentResolver.registerContentObserver(uri, false, smSObserver)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(smSObserver)
        stopForeground(true)
    }


    fun setOnNewMessageListener(l: OnNewMessageListener?) {
        this.onNewMessageListener = l
    }

//    fun setNoficationText(subTitle: String, message: String) {
//        if (notification != null) {
//            notification.text
//        }
//    }

}


class SMSBinder constructor(private val service: SMSHandleService) : Binder() {

    fun loadNews(): Boolean {
        return service.loadNewSMS()
    }

    fun setOnNewMessageListener(l: OnNewMessageListener?) {
        service.setOnNewMessageListener(l)
    }

//    fun setNoficationText(subTitle: String, message: String) {
//        service.setNoficationText(subTitle, message)
//    }
}


interface OnNewMessageListener {
    fun onNewMessage()
}