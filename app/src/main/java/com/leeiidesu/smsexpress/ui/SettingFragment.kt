package com.leeiidesu.smsexpress.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.View
import com.leeiidesu.smsexpress.R

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

//        findPreference("")
    }


}