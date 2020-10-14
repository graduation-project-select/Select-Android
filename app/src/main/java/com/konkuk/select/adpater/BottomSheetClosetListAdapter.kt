package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Closet

class BottomSheetClosetListAdapter(val closetList:ArrayList<Closet>):RecyclerView.Adapter<BottomSheetClosetListAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Closet, position: Int)
    }

    inner class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var tv_count: TextView = itemView.findViewById(R.id.tv_count)
        var iv_playbtn: ImageView = itemView.findViewById(R.id.iv_playbtn)

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, closetList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.row_bottom_sheet_closet_list, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tv_title.text = closetList[position].title
        holder.tv_count.text = closetList[position].count.toString()
    }

    override fun getItemCount(): Int {
        return closetList.size
    }


}