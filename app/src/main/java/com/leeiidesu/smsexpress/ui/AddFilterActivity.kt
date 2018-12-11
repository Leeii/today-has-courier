package com.leeiidesu.smsexpress.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.leeiidesu.smsexpress.App
import com.leeiidesu.smsexpress.R
import com.leeiidesu.smsexpress.model.Filter
import kotlinx.android.synthetic.main.activity_add_filter.*
import java.util.regex.Pattern

class AddFilterActivity : AppCompatActivity() {
    private var startColor = Color.parseColor("#2c2c7d")
    private var endColor = Color.parseColor("#de4f33")

    private var mToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_filter)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "过滤器"
        toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.done -> {
                    // 添加 修改完成

                    if (alias.text.isEmpty()) {
                        showToast("请输入别名")
                        return@setOnMenuItemClickListener true
                    }

                    val smsText = sms_matcher.text.toString()

                    val codeMatcher = code_matcher.text.toString()

                    val expressName = fixedName.text.toString()
                    val expressMatcher = name_regex.text.toString()
                    val start = splitStart.text.toString().toInt()
                    val end = splitEnd.text.toString().toInt()

                    val filter = Filter(0, alias.text.toString(), smsText, codeMatcher, expressName, fixed.isChecked,
                            expressMatcher, start, end, 0, startColor, endColor)
                    val boxFor = (application as App).boxStore!!.boxFor(Filter::class.java)

                    boxFor.put(filter)
                    showToast("添加/修改成功")
                    setResult(Activity.RESULT_OK)
                    finish()
                    true
                }
                else ->
                    true
            }
        }

        fixed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fixedName.visibility = View.VISIBLE
                rangeName.visibility = View.GONE
            } else {
                fixedName.visibility = View.GONE
                rangeName.visibility = View.VISIBLE
            }
        }

        val drawable = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColor, endColor))

        drawable.cornerRadius = resources.displayMetrics.density * 8

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

        test.setOnClickListener {
            confirmInput { n, c ->
                name.text = n
                code.text = c
                showToast("成功，请查看卡片效果")
            }
        }
    }

    private fun confirmInput(any: (String, String) -> Unit) {
        val smsText = sms.text.toString()

        if (smsText.contains(Regex(sms_matcher.text.toString()))) {
            val codeMatcher = code_matcher.text.toString()

            if (smsText.contains(Regex(codeMatcher))) {
                val expressName: String?
                expressName = if (!fixed.isChecked) {
                    val expressMatcher = name_regex.text.toString()
                    val start = splitStart.text.toString().toInt()
                    val end = splitEnd.text.toString().toInt()
                    findExpressName(smsText, expressMatcher, start, end)
                } else {
                    fixedName.text.toString()
                }

                if (expressName == null) {
                    showToast("没找到快递公司名称")
                } else {
                    val findGetCode = findGetCode(smsText, codeMatcher)
                    any(expressName, findGetCode!!)
                }
            } else {
                showToast("短信中没有匹配的Code!")
            }
        } else {
            showToast("该短信不匹配")
        }
    }


    private fun findExpressName(body: String?, expressMatcher: String, start: Int, end: Int): String? {
        if (body == null)
            return null
        val compile = Pattern.compile(expressMatcher).matcher(body)

        if (compile.find()) {
            val group = compile.group()
            return group.substring(start, group.length - end)
        }
        return null
    }

    private fun findGetCode(body: String?, regex: String): String? {
        val p = Pattern.compile(regex)
        val matcher = p.matcher(body)
        if (matcher.find()) {
            return matcher.group()
        }

        return null
    }

    fun showToast(msg: String) {
        mToast?.cancel()
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)

        mToast!!.show()
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