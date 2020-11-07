package com.konkuk.select.adpater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.utils.StaticValues


class CategoryPickerAdapter(var categoryList: ArrayList<String>): RecyclerView.Adapter<CategoryPickerAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    var subCategoryPickerAdapterArray: ArrayList<SubCategoryPickerAdapter> = arrayListOf()
    var subCategoryListArray: ArrayList<ArrayList<String>> = arrayListOf()

    interface OnItemClickListener {
        fun OnClickItem(category: String, subCategory: String)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_category: TextView = itemView.findViewById(R.id.tv_category)
        var rv_subCategory: RecyclerView = itemView.findViewById(R.id.rv_subCategory)

        init {
            tv_category.setOnClickListener {
                Log.d("TAG", "tv_category.setOnClickListener")
                if(rv_subCategory.visibility == View.GONE) rv_subCategory.visibility = View.VISIBLE
                else if(rv_subCategory.visibility == View.VISIBLE) rv_subCategory.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        initAdapter()
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_category_picker, parent, false)
        return ItemHolder(v)
    }

    private fun initAdapter(){
        for((index, c) in categoryList.withIndex()){
            subCategoryListArray.add(StaticValues.subCategoryList[categoryList[index]]!!)
            subCategoryPickerAdapterArray.add(SubCategoryPickerAdapter(categoryList[index], subCategoryListArray[index]))
            Log.d("TAG", (subCategoryListArray[index]).toString())
            subCategoryPickerAdapterArray[index].itemClickListener = object :SubCategoryPickerAdapter.OnItemClickListener{
                override fun OnClickItem(
                    holder: SubCategoryPickerAdapter.ItemHolder,
                    view: View,
                    category: String,
                    subCategory: String,
                    position: Int
                ) {
                    itemClickListener?.OnClickItem(category, subCategory)
                    Log.d("TAG", "category: $category, subCategory: $subCategory")
                }
            }

        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tv_category.text = categoryList[position]
        holder.rv_subCategory.layoutManager = LinearLayoutManager(holder.tv_category.context, LinearLayoutManager.VERTICAL, false)
        holder.rv_subCategory.adapter = subCategoryPickerAdapterArray[position]
    }

}