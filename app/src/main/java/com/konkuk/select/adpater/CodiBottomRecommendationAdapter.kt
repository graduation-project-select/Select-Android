package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import java.util.ArrayList

class CodiBottomRecommendationAdapter(var rcmdList: ArrayList<Clothes>):
    RecyclerView.Adapter<CodiBottomRecommendationAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Clothes, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_category_label: TextView = itemView.findViewById(R.id.tv_category_label)
        var iv_clothes_imgae: ImageView = itemView.findViewById(R.id.iv_img)
        init {
            itemView.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, rcmdList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.codi_bottom_recommendation_item, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tv_category_label.text = rcmdList[position].category
        Glide.with(holder.iv_clothes_imgae.context)
            .load(rcmdList[position].imgUri)
            .into(holder.iv_clothes_imgae)
    }

    override fun getItemCount(): Int {
        return rcmdList.size
    }
}