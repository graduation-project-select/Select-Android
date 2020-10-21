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

class ClothesItemAdapter(val ctx:Context,val item: ArrayList<Clothes>) : RecyclerView.Adapter<ClothesItemAdapter.ItemHolder>(){

    var itemClickListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ClothesItemAdapter.ItemHolder, view: View, data: Clothes, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clothesImg: ImageView = itemView.findViewById(R.id.clothcodi_iv)
        init {
            itemView.setOnClickListener{
                itemClickListener?.OnClickItem(this, it, item[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClothesItemAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.box_clothcodi_item, parent, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ClothesItemAdapter.ItemHolder, position: Int) {
        Glide.with(ctx)
            .load(item[position].imgUri)
            .into(holder.clothesImg)
    }
}