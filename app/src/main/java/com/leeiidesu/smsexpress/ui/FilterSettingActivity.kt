package com.leeiidesu.smsexpress.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.leeiidesu.smsexpress.App
import com.leeiidesu.smsexpress.R
import com.leeiidesu.smsexpress.model.Filter
import kotlinx.android.synthetic.main.activity_filter_setting.*

class FilterSettingActivity : AppCompatActivity() {
    private var mDataList: ArrayList<Filter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_setting)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "过滤器设置"
        toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.add -> {
                    startActivity(Intent(this, AddFilterActivity::class.java))
                    true
                }
                else ->
                    true
            }
        }

        mDataList.addAll((application as App).boxStore!!.boxFor(Filter::class.java).all)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MyAdapter(mDataList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0x10 && resultCode == Activity.RESULT_OK) {
            mDataList.clear()
            mDataList.addAll((application as App).boxStore!!.boxFor(Filter::class.java).all)
            recycler.adapter.notifyDataSetChanged()
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
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }
}

class MyAdapter(var mDataList: ArrayList<Filter>) : RecyclerView.Adapter<MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_filter, parent, false)

        return MyHolder(inflate)
    }


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val sms = mDataList[position]
        val drawable = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                intArrayOf(sms.startColor, sms.endColor))
        holder.container.background = drawable
        holder.alias.text = sms.alias
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }
}

class MyHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    var alias: TextView = view.findViewById(R.id.alias)
    var container: View = view.findViewById(R.id.container)
}
