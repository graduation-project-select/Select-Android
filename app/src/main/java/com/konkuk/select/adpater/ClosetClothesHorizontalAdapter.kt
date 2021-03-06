package com.konkuk.select.adpater

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.activity.DetailClothesActivity
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase

class ClosetClothesHorizontalAdapter(var categoryList:ArrayList<Category>, private val closetId:String = "", private val uid:String):
    RecyclerView.Adapter<ClosetClothesHorizontalAdapter.RVHolder>() {

    var closetClothesHorizontalItemAdapter: ArrayList<ClosetClothesHorizontalItemAdapter> = arrayListOf()
    var closetClothesHorizontalItemAdapterList: ArrayList<ArrayList<Clothes>> = arrayListOf()

    inner class RVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_of_rv:RecyclerView = itemView.findViewById(R.id.rv_of_rv)
        var underLine:View = itemView.findViewById(R.id.underLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        initAdapter()
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_horizontal, parent, false)
        return RVHolder(v)
    }

    private fun initAdapter(){
        for((index, c) in categoryList.withIndex()){
            closetClothesHorizontalItemAdapterList.add(arrayListOf())
            closetClothesHorizontalItemAdapter.add(ClosetClothesHorizontalItemAdapter(closetClothesHorizontalItemAdapterList[index]))
            fetchClothesData(c.label, index, closetId, uid)
            closetClothesHorizontalItemAdapter[index].itemClickListener = object:ClosetClothesHorizontalItemAdapter.OnItemClickListener{
                override fun onClickItem(holder: ClosetClothesHorizontalItemAdapter.ImageHolder, view: View, data: Clothes, position: Int) {
                    var intent = Intent(view.context, DetailClothesActivity::class.java)
                    intent.putExtra("clothesObj", data)
//                    intent.putExtra("isSharing", true)
                    view.context.startActivity(intent)
                }

            }

        }
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        if(categoryList[position].checked){
            holder.rv_of_rv.visibility = View.VISIBLE
            holder.underLine.visibility = View.VISIBLE
            holder.rv_of_rv.layoutManager = LinearLayoutManager(holder.rv_of_rv.context, LinearLayoutManager.HORIZONTAL, false)
            holder.rv_of_rv.adapter = closetClothesHorizontalItemAdapter[position]
        }else{
            holder.rv_of_rv.visibility = View.GONE
            holder.underLine.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    private fun fetchClothesData(category:String, index:Int, closetId: String, userId:String){
        var clothesRef = Fbase.CLOTHES_REF
            .whereEqualTo("category", category)
            .whereEqualTo("uid", userId)
        // 옷장이 선택된 경우
        if(closetId != "") clothesRef = clothesRef.whereArrayContains("closet", closetId)
        clothesRef.get()
            .addOnSuccessListener { documents ->
                closetClothesHorizontalItemAdapterList[index].clear()
                for (document in documents) {
                    val clothesObj = Fbase.getClothes(document)
                    clothesObj?.let { closetClothesHorizontalItemAdapterList[index].add(it) }
                }
                closetClothesHorizontalItemAdapter[index].notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents: ", exception)
            }
    }

}