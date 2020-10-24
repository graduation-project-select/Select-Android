package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.adpater.NotificationListAdapter
import com.konkuk.select.model.Notification
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.android.synthetic.main.toolbar.view.*

class NotificationListActivity : AppCompatActivity() {

    lateinit var notificatinoListAdapter:NotificationListAdapter
    var notiList = ArrayList<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        setToolbar()
        getNotiList()
        setAdapter()
    }

    fun setToolbar(){
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.title_tv.text = "알림"
        toolbar.right_iv.visibility = View.INVISIBLE
    }

    fun setAdapter(){
        rv_notification.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        notificatinoListAdapter = NotificationListAdapter(notiList)
        rv_notification.adapter = notificatinoListAdapter
        notificatinoListAdapter.itemClickListener = object :NotificationListAdapter.OnItemClickListener{
            override fun onClickItem(holder: NotificationListAdapter.ClosetShareViewHolder, view: View, data: Notification, position: Int) {
                var nextIntent = Intent(this@NotificationListActivity, CodiSuggestionListActivity::class.java)
                nextIntent.putExtra("codiSugNotiId", data.notiRef.id)
                startActivity(nextIntent)
            }
        }
    }

    fun getNotiList(){
        Fbase.NOTIFICATION_REF
            .whereEqualTo("uid", Fbase.uid)
            .get().addOnSuccessListener {
                notiList.clear()
                for(noti in it){
                    val notiObj = Fbase.getNotification(noti)
                    notiList.add(notiObj)
                }
                notificatinoListAdapter.notifyDataSetChanged()
            }
    }


}
