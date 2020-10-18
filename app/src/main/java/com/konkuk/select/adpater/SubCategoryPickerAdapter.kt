package com.konkuk.select.adpater

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import kotlinx.android.synthetic.main.item_category_picker.view.*

class SubCategoryPickerAdapter(val category:String, var subCategoryList: ArrayList<String>): RecyclerView.Adapter<SubCategoryPickerAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: SubCategoryPickerAdapter.ItemHolder, view: View, category:String, subCategory: String, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_category: TextView = itemView.findViewById(R.id.tv_category)

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, category, subCategoryList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_category_picker, parent, false)
        v.tv_category.textSize = 12f
        v.tv_category.setTextColor(Color.parseColor("#A13A3A3A"))
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tv_category.text = subCategoryList[position]
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }
}