package com.leeiidesu.smsexpress.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.leeiidesu.smsexpress.R
import kotlinx.android.synthetic.main.activity_add_filter.*

class AddFilterActivity : AppCompatActivity() {
    private var startColor = Color.parseColor("#2c2c7d")
    private var endColor = Color.parseColor("#de4f33")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_filter)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "过滤器"
        toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.done -> {
                    // 添加 修改完成
                    true
                }
                else ->
                    true
            }
        }

        val drawable = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColor, endColor))

        drawable.cornerRadius = 24f

        view.background = drawable


        startColorPick.setBarListener {
            startColor = it
            drawable.colors = intArrayOf(
                    startColor,
                    endColor
            )

            view.background = drawable
        }

        endColorPick.setBarListener {
            endColor = it
            drawable.colors = intArrayOf(
                    startColor,
                    endColor
            )

            view.background = drawable
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_filter, menu)
        return true
    }
}