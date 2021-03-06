package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Closet

class ClosetListBlockAdapter(val closetList:ArrayList<Closet>):
    RecyclerView.Adapter<ClosetListBlockAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClickItem(holder: ItemHolder, view: View, data: Closet, position: Int)
        fun onClickShareBtn(holder: ItemHolder, view: View, data: Closet, position: Int)
        fun onClickSettingBtn(holder: ItemHolder, view: View, data: Closet, position: Int)
    }

    inner class ItemHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        var iv_img:ImageView = itemView.findViewById(R.id.iv_img)
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var tv_count: TextView = itemView.findViewById(R.id.tv_count)
        var iv_sharebtn: ImageView = itemView.findViewById(R.id.iv_sharebtn)
        var iv_settingbtn: ImageView = itemView.findViewById(R.id.iv_settingbtn)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onClickItem(this, it, closetList[adapterPosition], adapterPosition)
            }
            iv_sharebtn.setOnClickListener {
                itemClickListener?.onClickShareBtn(this, it, closetList[adapterPosition], adapterPosition)
            }
            iv_settingbtn.setOnClickListener {
                itemClickListener?.onClickSettingBtn(this, it, closetList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_block_item, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
//        holder.iv_img 에 closetList[position].imgUrl 넣기
        holder.tv_title.text = closetList[position].name
        holder.tv_count.text = closetList[position].count.toString()
    }

    override fun getItemCount(): Int {
        return closetList.size
    }
}