package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.model.Clothes

class CodiBottomClothesLinearAdapter(val ctx:Context, val clothesList:ArrayList<Clothes>): RecyclerView.Adapter<CodiBottomClothesLinearAdapter.ItemHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Clothes, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_clothes_img: ImageView = itemView.findViewById(R.id.iv_clothes_img)
        init {
            itemView.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, clothesList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.codi_bottom_clothes_item_linear, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
//        holder.iv_clothes_img 에 src넣어주기
//        holder.iv_clothes_img.setImageResource(R.drawable.cloth_test)
        Glide.with(ctx)
            .load(clothesList[position].img)
            .into(holder.iv_clothes_img)
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }
}