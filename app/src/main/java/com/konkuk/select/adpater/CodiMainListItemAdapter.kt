package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.model.Codi

class CodiMainListItemAdapter(val ctx:Context, val codiList: ArrayList<Codi>) :
    RecyclerView.Adapter<CodiMainListItemAdapter.CodiHolder>() {

    inner class CodiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var codi_iv: ImageView = itemView.findViewById(R.id.codi_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodiHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_codi_main_list_item, parent, false)
        return CodiHolder(v)
    }

    override fun onBindViewHolder(holder: CodiHolder, position: Int) {
        Glide.with(ctx)
            .load(codiList[position].imgUri)
            .into(holder.codi_iv)
    }

    override fun getItemCount(): Int {
        return codiList.size
    }
}