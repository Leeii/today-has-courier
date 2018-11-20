package com.leeiidesu.smsexpress.ui

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CheckedTextView
import android.widget.TextView
import android.widget.Toast
import com.leeiidesu.smsexpress.*
import com.leeiidesu.smsexpress.model.SMS
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var smsBinder: SMSBinder? = null

    private var mBox: BoxStore? = null
    private val mDataList = ArrayList<SMS>()


    private var pageIndex = 1

    private lateinit var mAdapter: Adapter

    private var lastClick: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.title = "今天有老婆的快递吗"


        fab.setOnClickListener { view ->
            if (System.currentTimeMillis() - lastClick > 2000) {
                Toast.makeText(view.context, "再按一次确认已取选中包裹..", Toast.LENGTH_SHORT)
                        .show()
                lastClick = System.currentTimeMillis()
                return@setOnClickListener
            }
            if (mAdapter.mSelectedItems.size > 0) {
                for (s in mAdapter.mSelectedItems) {
                    s.got = 1
                }
                val boxFor = mBox!!.boxFor(SMS::class)
                boxFor.put(mAdapter.mSelectedItems)

                mAdapter.mSelectedItems.clear()
                mAdapter.notifyDataSetChanged()
            }
        }


        refresh.setOnRefreshListener {
            pageIndex = 1

            getDataList()
        }

        refresh.setOnLoadMoreListener {
            pageIndex++
            getDataList()
        }

        bindService(Intent(this, SMSHandleService::class.java), object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                smsBinder?.setOnNewMessageListener(null)
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                smsBinder = service as SMSBinder?

                smsBinder?.setOnNewMessageListener(object : OnNewMessageListener {
                    override fun onNewMessage() {
                        refresh.autoRefresh()
                    }
                })

                smsBinder?.loadNews()
            }

        }, Context.BIND_AUTO_CREATE)

        recycler.layoutManager = LinearLayoutManager(this)
        mAdapter = Adapter(mDataList)
        recycler.adapter = mAdapter

        getDataList()
    }


    private fun getDataList() {
        if (mBox == null) {
            mBox = (application as App).boxStore
        }

        val boxFor = mBox!!.boxFor(SMS::class)
        val find = boxFor.query()
                .orderDesc(SMS_.date)
                .orderDesc(SMS_.got)
                .build()
                .find((pageIndex - 1) * 20L, 20)
        if (pageIndex == 1) {
            mDataList.clear()
        }
        mDataList.addAll(find)

        if (refresh.isRefreshing) {
            refresh.finishRefresh()
        }

        if (refresh.isLoading) {
            refresh.finishLoadMore()
        }
        refresh.isEnableLoadMore = find.size >= 20
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }

            R.id.action_new -> {
                smsBinder?.loadNews()
                true
            }
            R.id.action_all -> {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否一键将所有包裹设为领取？")
                        .setPositiveButton("确定") { dialog, which ->
                            val boxFor = mBox!!.boxFor(SMS::class)
                            val all = boxFor.all
                            for (sms in all) {
                                sms.got = 1
                            }

                            boxFor.put(all)

                            refresh.autoRefresh()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)


        }
    }
}

class Adapter(var mDataList: ArrayList<SMS>) : RecyclerView.Adapter<MyViewHolder>() {
    val mSelectedItems = ArrayList<SMS>()

    var format = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sms, parent, false)

        return MyViewHolder(inflate)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sms = mDataList[position]
        holder.time.text = format.format(Date(sms.date))
        holder.code.text = sms.code
        holder.status.text = if (sms.got == 1) "已取" else "未取"
        holder.status.setTextColor(Color.parseColor(if (sms.got == 1) "#999999" else "#ffffff"))

        holder.name.text = sms.express
        holder.checked.visibility = if (sms.got == 1) View.GONE else View.VISIBLE
        holder.checked.isChecked = mSelectedItems.contains(sms)

        holder.container.setBackgroundResource(if ("京东物流".equals(sms.express)) {
            R.drawable.shape_card_jd_bg
        } else {
            R.drawable.shape_card_taobao_bg
        })


        holder.itemView.setOnClickListener {
            val index = holder.adapterPosition
            if (index != -1) {
                val data = mDataList[index]
                if (data.got == 1) return@setOnClickListener
                if (mSelectedItems.contains(data)) {
                    mSelectedItems.remove(data)
                } else {
                    mSelectedItems.add(data)
                }

                notifyItemChanged(index)
            }
        }
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }
}

class MyViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    var time: TextView = view.findViewById(R.id.time)
    var code: TextView = view.findViewById(R.id.code)
    var status: TextView = view.findViewById(R.id.status)
    var checked: CheckedTextView = view.findViewById(R.id.checked)
    var name: TextView = view.findViewById(R.id.name)
    var container: View = view.findViewById(R.id.container)
}
