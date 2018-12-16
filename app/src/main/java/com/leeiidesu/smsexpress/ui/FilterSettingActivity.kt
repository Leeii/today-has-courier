package com.leeiidesu.smsexpress.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.leeiidesu.smsexpress.App
import com.leeiidesu.smsexpress.R
import com.leeiidesu.smsexpress.model.Filter
import com.leeiidesu.smsexpress.model.SMS
import com.leeiidesu.smsexpress.model.SMS_
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
                    startActivityForResult(Intent(this, AddFilterActivity::class.java), 0x20)
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
        if (requestCode == 0x20 && resultCode == Activity.RESULT_OK) {
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
        val filter = mDataList[position]
        val drawable = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                intArrayOf(filter.startColor, filter.endColor))
        holder.container.background = drawable
        holder.alias.text = filter.alias

        holder.itemView.setOnClickListener {
            var context = it.context as Activity
            val intent = Intent(context, AddFilterActivity::class.java)
            intent.putExtra("data", filter)
            context.startActivityForResult(intent, 0x20)

        }


        holder.itemView.setOnLongClickListener {

            var context = it.context as FilterSettingActivity
            AlertDialog.Builder(context)
                    .setMessage("是否删除本条过滤器？本条过滤器过滤的短信也会随之删除！")
                    .setTitle("删除")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        val smsBox = (context.application as App).boxStore!!.boxFor(SMS::class.java)
                        val smsList = smsBox.query()
                                .equal(SMS_.filterId, filter.id)
                                .build().find()

                        smsBox.remove(smsList)

                        val boxFor = (context.application as App).boxStore!!.boxFor(Filter::class.java)

                        boxFor.remove(filter)

                        mDataList.remove(filter)

                        notifyItemRemoved(position)


                        Toast.makeText(context, "已删除", Toast.LENGTH_SHORT)
                                .show()
                    }
                    .show()


            true
        }
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }
}

class MyHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    var alias: TextView = view.findViewById(R.id.alias)
    var container: View = view.findViewById(R.id.container)
}
