package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Notification
import com.konkuk.select.network.Fbase

class NotificationListAdapter(var notiList:ArrayList<Notification>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var notiIcon:ArrayList<Int> = arrayListOf( R.drawable.closet_share_icon)

    private val CLOSET_SHARE= 0
    private val FOLLOWING= 1

    var itemClickListener: OnItemClickListener ?= null

    interface OnItemClickListener {
        fun onClickItem(holder: ClosetShareViewHolder, view: View, data: Notification, position: Int)
    }

    inner class ClosetShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconImg: ImageView = itemView.findViewById(R.id.imageView)
        var senderName: TextView = itemView.findViewById(R.id.tv_senderName)
        init {
            itemView.setOnClickListener {
                itemClickListener?.onClickItem(this, it, notiList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(notiList[position].type){
            "CLOSET_SHARE" -> {
                CLOSET_SHARE
            }
            "FOLLOWING" -> {
                FOLLOWING
            }
            else -> {
                CLOSET_SHARE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            CLOSET_SHARE -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_item, parent, false)
                return ClosetShareViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_item, parent, false)
                return ClosetShareViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            CLOSET_SHARE -> {
                var holder:ClosetShareViewHolder = holder as ClosetShareViewHolder
                holder.iconImg.setImageResource(notiIcon[CLOSET_SHARE])
//                holder.senderName.text = "최서희"
                setSenderName(notiList[position].notiRef.id, holder)
            }
            else -> {
                var holder:ClosetShareViewHolder = holder as ClosetShareViewHolder
                holder.iconImg.setImageResource(R.drawable.closet_share_icon)
                holder.senderName.text = "-"
            }
        }
    }

    override fun getItemCount(): Int {
        return notiList.size
    }

    fun setSenderName(notiId:String, holder: ClosetShareViewHolder){
        Fbase.CODISUG_NOTI_REF.document(notiId)
            .get().addOnSuccessListener {
                val codiSugNotiObj = Fbase.getCodiSugNoti(it)
                val senderUid = codiSugNotiObj.senderUid
                Fbase.USERS_REF.document(senderUid).get().addOnSuccessListener {
                    if(it.exists()){
                        val userName = it.get("name") as String  // 코디 추천 보낸사람 이름
                        holder.senderName.text = userName
                    }
                }
            }
    }

}