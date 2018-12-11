package com.leeiidesu.smsexpress

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.leeiidesu.smsexpress.model.Filter
import com.leeiidesu.smsexpress.model.SMS
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import java.util.regex.Pattern

class SMSHandleService : Service() {
    private val mBinder = SMSBinder(this)
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
        val projection = arrayOf("address", "person", "date", "type", "body", "subject")

        val filterBox = boxStore!!.boxFor(Filter::class)
        val all = filterBox.all

        var refresh = false
        if (!all.isEmpty()) {
            for (filter in all) {
                val query = contentResolver.query(uri,
                        projection,
                        "date>? AND body LIKE ?",
                        arrayOf("${filter.lastReadTime}", "%${filter.keywordsRegex}%"),
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

                        val code = findGetCode(body, filter)
                        val expressName = findExpressName(body, filter)
                        val sms = SMS(0, address, person, date, type, body, subject, code, expressName)
                        sms.filter!!.target = filter
                        smsList.add(sms)
                    }
                    query.close()

                    val boxFor = boxStore!!.boxFor(SMS::class)
                    boxFor.put(smsList)

                    filter.lastReadTime = System.currentTimeMillis()
                    filterBox.put(filter)


                    refresh = true
                }
                query?.close()
            }
            if (refresh) {
                onNewMessageListener?.onNewMessage()
            }
        }

        return refresh
    }

    private fun findExpressName(body: String?, filter: Filter): String? {
        if (body == null)
            return null
        if (filter.fixedName) {
            return filter.nominate
        }

        val compile = Pattern.compile(filter.nameRegex).matcher(body)

        if (compile.find()) {
            val group = compile.group()
            return group.substring(filter.nameStartSplit, group.length - filter.nameEndSplit)
        }
        return null
    }

    private fun findGetCode(body: String?, filter: Filter): String? {
        val p = Pattern.compile(filter.codeRegex)
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
}


class SMSBinder constructor(private val service: SMSHandleService) : Binder() {

    fun loadNews(): Boolean {
        return service.loadNewSMS()
    }

    fun setOnNewMessageListener(l: OnNewMessageListener?) {
        service.setOnNewMessageListener(l)
    }
}


interface OnNewMessageListener {
    fun onNewMessage()
}