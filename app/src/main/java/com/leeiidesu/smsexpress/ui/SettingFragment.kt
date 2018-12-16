package com.leeiidesu.smsexpress.ui

import android.app.AlertDialog
import android.arch.lifecycle.LiveData
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.View
import android.widget.Toast
import com.leeiidesu.smsexpress.App
import com.leeiidesu.smsexpress.R
import com.leeiidesu.smsexpress.model.Filter
import com.leeiidesu.smsexpress.model.SMS

class SettingFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesMode = MODE_PRIVATE
        addPreferencesFromResource(R.xml.setting)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference("filter")
                .setOnPreferenceClickListener {
                    startActivity(Intent(context, FilterSettingActivity::class.java))
                    true
                }
        findPreference("clean")
                .setOnPreferenceClickListener {
                    AlertDialog.Builder(activity)
                            .setMessage("确定清除全部数据吗？")
                            .setTitle("提示")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { _, _ ->
                                val boxFor = (activity.application as App).boxStore!!.boxFor(SMS::class.java)

                                boxFor.removeAll()


                                Toast.makeText(activity, "数据已清除", Toast.LENGTH_SHORT)
                                        .show()
                            }
                            .show()



                    true
                }
        findPreference("reset")
                .setOnPreferenceClickListener {
                    AlertDialog.Builder(activity)
                            .setMessage("将可能造成数据重复，确定重置所有时间吗？")
                            .setTitle("提示")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { _, _ ->
                                val boxFor = (activity.application as App).boxStore!!.boxFor(Filter::class.java)

                                val all = boxFor.all
                                for (f in all) {
                                    f.lastReadTime = 0
                                }

                                boxFor.put(all)


                                Toast.makeText(activity, "时间已重置", Toast.LENGTH_SHORT)
                                        .show()
                            }
                            .show()
                    true
                }

    }


}