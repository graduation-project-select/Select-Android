package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Codi

class CodiListAdapter (val item: ArrayList<Codi>) : RecyclerView.Adapter<CodiListAdapter.ItemHolder>() {

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.codi_iv)
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
//        holder.img.setImageResource(item[position].img)
    }

}