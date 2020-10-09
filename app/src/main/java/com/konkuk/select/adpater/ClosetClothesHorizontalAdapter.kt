package com.konkuk.select.adpater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.konkuk.select.R
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import org.json.JSONObject
import kotlin.random.Random

class ClosetClothesHorizontalAdapter(val ctx: Context, var categoryList:ArrayList<Category>):
    RecyclerView.Adapter<ClosetClothesHorizontalAdapter.RVHolder>() {

    private var db = FirebaseFirestore.getInstance()
    var closetClothesHorizontalItemAdapter: ArrayList<ClosetClothesHorizontalItemAdapter> = arrayListOf()
    var closetClothesHorizontalItemAdapterList: ArrayList<ArrayList<Clothes>> = arrayListOf()

    inner class RVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_of_rv:RecyclerView = itemView.findViewById(R.id.rv_of_rv)
        var underLine:View = itemView.findViewById(R.id.underLine)
    }

    private fun initAdapter(){
        for((index, c) in categoryList.withIndex()){
            closetClothesHorizontalItemAdapterList.add(arrayListOf())
            closetClothesHorizontalItemAdapter.add(ClosetClothesHorizontalItemAdapter(ctx, closetClothesHorizontalItemAdapterList[index]))
            fetchClothesData(c.label, index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        initAdapter()
        var v = LayoutInflater.from(parent.context).inflate(R.layout.closet_clothes_rv_item_horizontal, parent, false)
        return RVHolder(v)
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        if(categoryList[position].checked){
            holder.rv_of_rv.visibility = View.VISIBLE
            holder.underLine.visibility = View.VISIBLE
            holder.rv_of_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            holder.rv_of_rv.adapter = closetClothesHorizontalItemAdapter[position]
        }else{
            holder.rv_of_rv.visibility = View.GONE
            holder.underLine.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    private fun fetchClothesData(category:String, index:Int){
        db.collection("clothes")
            .whereEqualTo("category", category)
            .whereEqualTo("uid", Fbase.uid)
            .get()
            .addOnSuccessListener { documents ->
                closetClothesHorizontalItemAdapterList[index].clear()
                for (document in documents) {
                    val jsonObj = JSONObject(document.data)
                    closetClothesHorizontalItemAdapterList[index].add(Clothes(document.id, category, jsonObj["imgUrl"].toString()))
                }
                closetClothesHorizontalItemAdapter[index].notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents: ", exception)
            }
    }

}