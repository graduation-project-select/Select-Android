package com.konkuk.select.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import kotlin.random.Random

class ClosetClothesHorizontalAdapter(val ctx: Context, var categoryList:ArrayList<Category>):
    RecyclerView.Adapter<ClosetClothesHorizontalAdapter.RVHolder>() {

    lateinit var closetClothesHorizontalItemAdapter: ClosetClothesHorizontalItemAdapter

    inner class RVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_of_rv:RecyclerView = itemView.findViewById(R.id.rv_of_rv)
        var underLine:View = itemView.findViewById(R.id.underLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_horizontal, parent, false)
        return RVHolder(v)
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        if(categoryList[position].checked){
            holder.rv_of_rv.visibility = View.VISIBLE
            holder.underLine.visibility = View.VISIBLE
            holder.rv_of_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            closetClothesHorizontalItemAdapter = ClosetClothesHorizontalItemAdapter(ctx, makeClothesData(categoryList[position].label))
            holder.rv_of_rv.adapter = closetClothesHorizontalItemAdapter
        }else{
            holder.rv_of_rv.visibility = View.GONE
            holder.underLine.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    // temp (data fetch 필요)
    fun makeClothesData(category:String):ArrayList<Clothes>{
        var c_arr:ArrayList<Clothes> = arrayListOf()
        for(n in 0..Random.nextInt(1,10)){
            c_arr.add(Clothes(n.toString(), category, ""))
        }
        return c_arr
    }

}