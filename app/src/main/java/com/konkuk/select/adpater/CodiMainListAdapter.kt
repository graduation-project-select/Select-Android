package com.konkuk.select.adpater

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.konkuk.select.R
import com.konkuk.select.activity.DetailCodiActivity
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.CODI_REF
import com.konkuk.select.network.Fbase.USERS_REF

class CodiMainListAdapter(val codiTagRefList:ArrayList<DocumentReference>, var public:Boolean = false):RecyclerView.Adapter<CodiMainListAdapter.ListHolder>() {

    var codiMainListItemAdapterArray: ArrayList<CodiMainListItemAdapter> = arrayListOf()
    var codiListArray: ArrayList<ArrayList<Codi>> = arrayListOf()

    inner class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var rv_codiList: RecyclerView = itemView.findViewById(R.id.rv_codiList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        initAdapter()
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.item_codi_main_list, parent, false)
        return ListHolder(v)
    }

    private fun initAdapter(){
        for((index, tagRef) in codiTagRefList.withIndex()){
            codiListArray.add(arrayListOf())
            codiMainListItemAdapterArray.add(CodiMainListItemAdapter(codiListArray[index]))
            getCodiListByTag(tagRef, index)
        }
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        codiTagRefList[position].get().addOnSuccessListener {
            holder.tv_title.text = it["name"] as String
            holder.rv_codiList.layoutManager = LinearLayoutManager(holder.rv_codiList.context, LinearLayoutManager.HORIZONTAL, false)
            holder.rv_codiList.adapter = codiMainListItemAdapterArray[position]
            codiMainListItemAdapterArray[position].itemClickListener = object:CodiMainListItemAdapter.OnItemClickListener{
                override fun OnClickItem(
                    holder: CodiMainListItemAdapter.CodiHolder,
                    view: View,
                    data: Codi,
                    position: Int
                ) {
                    val intent = Intent(view.context, DetailCodiActivity::class.java)
                    intent.putExtra("codiId", data.id)
                    view.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return codiTagRefList.size
    }

    private fun getCodiListByTag(tagRef: DocumentReference, index:Int) {
        tagRef.get().addOnSuccessListener {
            var codi_ref = CODI_REF
                .whereArrayContains("tags", tagRef)
                .whereEqualTo("uid", Fbase.uid)

            if(public) codi_ref.whereEqualTo("public", true)

            codi_ref.orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { documents ->
                    // 비어있으면 유저 코디태그 삭제
                    if(documents.isEmpty){
                        deleteUserCodiTag(tagRef)
                    }

                    codiListArray[index].clear()
                    for (document in documents) {
                        val codiObj = Fbase.getCodi(document)
                        codiListArray[index].add(codiObj)
                    }
                    codiMainListItemAdapterArray[index].notifyDataSetChanged()
                }
        }
    }

    fun deleteUserCodiTag(tagRef:DocumentReference){
        Fbase.uid?.let {
            USERS_REF.document(it)
                .update("codiTagList", FieldValue.arrayRemove(tagRef))
                .addOnSuccessListener {
                    Log.v("delete codi empty tag", "${tagRef.id}")
                }
        }
    }
}