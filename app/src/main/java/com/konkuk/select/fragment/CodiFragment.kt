package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.konkuk.select.R
import com.konkuk.select.activity.DetailCodiActivity
import com.konkuk.select.adpater.CodiMainListAdapter
import com.konkuk.select.adpater.CodiMainListItemAdapter
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiFragment : Fragment() {

    private lateinit var codiMainListAdapter:CodiMainListAdapter
    private lateinit var latestCodiListAdapter: CodiMainListItemAdapter
    var latestCodiList = ArrayList<Codi>()
    var myTagList: ArrayList<DocumentReference> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_codi, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingAdapter()
    }

    override fun onResume() {
        super.onResume()
        getLatestCodiList()
        getUserCodiTagList()  // TODO pull to refresh 로 새로고침구현하기
    }

    private fun settingAdapter(){
        rv_codiList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        codiMainListAdapter = CodiMainListAdapter(myTagList)
        rv_codiList.adapter = codiMainListAdapter

        rv_latestCodiList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        latestCodiListAdapter= CodiMainListItemAdapter(latestCodiList)
        rv_latestCodiList.adapter = latestCodiListAdapter

        latestCodiListAdapter.itemClickListener = object :CodiMainListItemAdapter.OnItemClickListener{
            override fun OnClickItem(holder: CodiMainListItemAdapter.CodiHolder, view: View, data: Codi, position: Int) {
                val intent = Intent(view.context, DetailCodiActivity::class.java)
                intent.putExtra("codiId", data.id)
                view.context.startActivity(intent)
            }
        }
    }

    private fun getLatestCodiList(){
        Fbase.CODI_REF
            .whereEqualTo("uid", Fbase.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                latestCodiList.clear()
                for ((index, document) in documents.withIndex()) {
                    if(index > 4) break //최신 데이터 최대 5개 보여줌

                    val codiObj = Fbase.getCodi(document)
                    latestCodiList.add(codiObj)
                }
                latestCodiListAdapter.notifyDataSetChanged()
            }
    }

    private fun getUserCodiTagList() {
        Fbase.uid?.let {
            Fbase.USERS_REF.document(it).get().addOnSuccessListener {
                if(it.get("codiTagList") != null){
                    val codiTagList = it.get("codiTagList") as ArrayList<DocumentReference>
                    myTagList.clear()
                    for (tag in codiTagList) {
                        myTagList.add(tag)
                    }
                    codiMainListAdapter.notifyDataSetChanged()
                }

            }
        }
    }

}
