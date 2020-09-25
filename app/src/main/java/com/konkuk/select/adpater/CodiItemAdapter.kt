package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi

class CodiItemAdapter(val item: ArrayList<Codi>) : RecyclerView.Adapter<CodiItemAdapter.ItemHolder>(){
    var itemClickListener: CodiItemAdapter.OnItemClickListener?=null

    interface OnItemClickListener {
        fun OnClickItem(holder: CodiItemAdapter.ItemHolder, view: View, data: Codi, position: Int)
    }


    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var codiImg: ImageView = itemView.findViewById(R.id.clothcodi_iv)
        init {
            itemView.setOnClickListener{
                itemClickListener?.OnClickItem(this, it, item[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CodiItemAdapter.ItemHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.box_clothcodi_item, parent, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: CodiItemAdapter.ItemHolder, position: Int) {
        holder.codiImg.setImageResource(R.drawable.codi_test)
    }
}