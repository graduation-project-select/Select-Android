package com.konkuk.select.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.fragment.CodiTagListFragment
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.CodiTag

class CodiTagListAdapter(val item: ArrayList<CodiTag>) : RecyclerView.Adapter<CodiTagListAdapter.ItemHolder>() {

    var itemClickListener: OnItemClickListener ?= null

    interface OnItemClickListener {
        fun OnClickItem(holder: CodiTagListAdapter.ItemHolder, view: View, data: CodiTag, position: Int)
    }

    fun onItemMove(pos1:Int, pos2:Int) {
        val item1 = item.get(pos1)
        item.removeAt(pos1)
        item.add(pos2, item1)
        notifyItemMoved(pos1, pos2)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag: TextView = itemView.findViewById(R.id.tag)
        init {
            tag.setOnClickListener {
                itemClickListener?.OnClickItem(this, it, item[adapterPosition], adapterPosition)
            }
        }
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