package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Clothes

class ClosetClothesHorizontalAdapter(val ctx: Context, var clothesList:ArrayList<ArrayList<Clothes>>):
    RecyclerView.Adapter<ClosetClothesHorizontalAdapter.RVHolder>() {

    lateinit var closetClothesHorizontalItemAdapter: ClosetClothesHorizontalItemAdapter

    inner class RVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_of_rv:RecyclerView = itemView.findViewById(R.id.rv_of_rv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_horizontal, parent, false)
        return RVHolder(v)
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        holder.rv_of_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        closetClothesHorizontalItemAdapter = ClosetClothesHorizontalItemAdapter(ctx, clothesList[position])
        holder.rv_of_rv.adapter = closetClothesHorizontalItemAdapter
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }
}