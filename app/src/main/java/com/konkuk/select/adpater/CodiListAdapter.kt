package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Codi

class CodiListAdapter (val item: ArrayList<Codi>) : RecyclerView.Adapter<CodiListAdapter.ItemHolder>() {

    var itemClickListener: CodiListAdapter.OnItemClickListener ?= null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: Codi, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var codiImg: ImageView = itemView.findViewById(R.id.codi_iv)
        init {
            codiImg.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, item[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CodiListAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.box_codi_img_list, parent, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: CodiListAdapter.ItemHolder, position: Int) {
//        holder.codiImg.setImageResource(android.R.drawable.ic_menu_gallery)
    }

}