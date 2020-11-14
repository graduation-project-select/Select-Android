package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R

class MyPageFeedAdapter(var items:ArrayList<String>): RecyclerView.Adapter<MyPageFeedAdapter.ImageHolder>() {

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ImageHolder, view: View, data: String, position: Int)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var feedImg: ImageView = itemView.findViewById(R.id.imageView)
        init {
            feedImg.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.simple_image_item, parent, false)
        return ImageHolder(v)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Glide.with(holder.feedImg.context)
            .load(items[position])
            .into(holder.feedImg)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}