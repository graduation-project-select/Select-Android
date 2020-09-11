package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R

class ClosetClothesVerticalAdapter(var items:ArrayList<String>): RecyclerView.Adapter<ClosetClothesVerticalAdapter.ImageHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ImageHolder, view: View, data: String, position: Int)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clothesImg: ImageView = itemView.findViewById(R.id.iv_clothesImg)
        init {
            clothesImg.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_vertical, parent, false)
        return ImageHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.clothesImg.setImageResource(android.R.drawable.ic_menu_gallery)
    }
}