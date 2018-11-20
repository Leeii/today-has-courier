package com.leeiidesu.smsexpress.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.leeiidesu.smsexpress.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "设置"

        var fragmentById = fragmentManager.findFragmentById(R.id.content)
        if (fragmentById == null) {
            fragmentById = SettingFragment()
            fragmentManager.beginTransaction()
                    .add(R.id.content, fragmentById)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
