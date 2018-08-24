package com.leeiidesu.smsexpress

import android.app.Application
import android.content.Intent
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.objectbox.BoxStore

class App : Application() {
    var boxStore: BoxStore? = null


    override fun onCreate() {
        super.onCreate()

        boxStore = MyObjectBox.builder()
                .androidContext(this)
                .build()

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(context) }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> ClassicsHeader(context) }


        startService(Intent(this, SMSHandleService::class.java))
    }
}