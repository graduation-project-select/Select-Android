package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.model.Clothes

class ClosetClothesHorizontalItemAdapter(var clothesList:ArrayList<Clothes>): RecyclerView.Adapter<ClosetClothesHorizontalItemAdapter.ImageHolder>()  {
    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ImageHolder, view: View, data:Clothes, position: Int)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clothesImg: ImageView = itemView.findViewById(R.id.iv_clothesImg)
        init {
            clothesImg.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, clothesList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_horizontal_item, parent, false)
        return ImageHolder(v)
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Glide.with(holder.clothesImg.context)
            .load(clothesList[position].imgUri)
            .into(holder.clothesImg)
    }
}