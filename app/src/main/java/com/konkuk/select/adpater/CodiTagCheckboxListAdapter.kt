package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.CodiTag


class CodiTagCheckboxListAdapter (val context: Context, var codiTagList: ArrayList<CodiTag>): RecyclerView.Adapter<CodiTagCheckboxListAdapter.ItemHolder>(){

    var itemClickListener:OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: ItemHolder, view: View, data: CodiTag, position: Int)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var codi_tag_cb: CheckBox = itemView.findViewById(R.id.codi_tag_cb)
        init {
            codi_tag_cb.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, codiTagList[adapterPosition], adapterPosition)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.box_coditag_checkbox, parent, false)
        return ItemHolder(v)
    }


    override fun getItemCount(): Int {
        return codiTagList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.codi_tag_cb.text = codiTagList[position].tag
    }

}