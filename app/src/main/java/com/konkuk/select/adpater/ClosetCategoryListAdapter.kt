package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R

class ClosetCategoryListAdapter(val context:Context, val categoryList: ArrayList<String>): RecyclerView.Adapter<ClosetCategoryListAdapter.ItemHolder>(){

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: String, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        init {
            tvCategory.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, categoryList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_category_rv_item, parent, false)
        return ItemHolder(v)
    }


    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tvCategory.text = categoryList[position]
    }


}