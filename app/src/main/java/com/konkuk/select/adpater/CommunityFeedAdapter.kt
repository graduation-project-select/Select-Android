package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.model.Feed

class CommunityFeedAdapter(val item: ArrayList<Feed>) : RecyclerView.Adapter<CommunityFeedAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityFeedAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.community_feed_list, parent, false)
        return ItemHolder(v)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var feedID: TextView = itemView.findViewById(R.id.feedID)
        var feedUserTag: TextView = itemView.findViewById(R.id.feed_user_tag)
        var feedImg: ImageView = itemView.findViewById(R.id.feed_image)
        var heartCount : TextView = itemView.findViewById(R.id.hear_count_tv)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: CommunityFeedAdapter.ItemHolder, position: Int) {
        holder.feedID.text = item[position].id
        holder.feedUserTag.text = item[position].tag
        holder.heartCount.text = "좋아요 " + item[position].heartCount + "개"
        Glide.with(holder.feedImg.context)
            .load(item[position].img)
            .into(holder.feedImg)
    }
}