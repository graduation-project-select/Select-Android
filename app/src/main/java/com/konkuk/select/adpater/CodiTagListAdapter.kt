package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.CodiTag

class CodiTagListAdapter(val item: ArrayList<CodiTag>) : RecyclerView.Adapter<CodiTagListAdapter.ItemHolder>(){

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag: TextView = itemView.findViewById(R.id.tag)
        var moveBtn: View = itemView.findViewById(R.id.moveBtn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CodiTagListAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.row_codi_tag_list, parent, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: CodiTagListAdapter.ItemHolder, position: Int) {
        holder.tag.text = item[position].tag
    }
}