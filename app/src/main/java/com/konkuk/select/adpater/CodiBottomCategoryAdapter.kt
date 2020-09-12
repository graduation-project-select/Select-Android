package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Category

class CodiBottomCategoryAdapter(var categoryList: ArrayList<Category>): RecyclerView.Adapter<CodiBottomCategoryAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Category, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_label: TextView = itemView.findViewById(R.id.tv_label)
        init {
            itemView.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, categoryList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CodiBottomCategoryAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.row_codi_bottom_category, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: CodiBottomCategoryAdapter.ItemHolder, position: Int) {
        holder.tv_label.text = categoryList[position].label

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}