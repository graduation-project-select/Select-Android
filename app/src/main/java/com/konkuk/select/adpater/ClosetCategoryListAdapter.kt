package com.konkuk.select.adpater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Category

class ClosetCategoryListAdapter(val context:Context, var categoryList: ArrayList<Category>): RecyclerView.Adapter<ClosetCategoryListAdapter.ItemHolder>(){

    var itemClickListener:OnItemClickListener?=null
    var checkedCount: MutableLiveData<Int> = MutableLiveData(initCheckedCount())

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Category, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cb_category: CheckBox = itemView.findViewById(R.id.cb_category)
        init {
            cb_category.setOnClickListener {
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
        holder.cb_category.text = categoryList[position].label
        holder.cb_category.isChecked = categoryList[position].checked
        holder.cb_category.setOnClickListener {
            if(categoryList[position].checked){
                categoryList[position].checked = false
                checkedCount.value = checkedCount.value?.minus(1)
                if(checkedCount.value!! <= 0){
                    categoryList[position].checked = true
                    checkedCount.value = 1
                }
            }else{
                categoryList[position].checked = true
                checkedCount.value = checkedCount.value?.plus(1)
            }
            holder.cb_category.isChecked = categoryList[position].checked

            Log.d("categoryList", "Adapter_categoryList: $categoryList")
        }
    }

    fun initCheckedCount():Int{
        var count = 0;
        for(c in categoryList){
            if(c.checked) count++
        }
        return count
    }

}