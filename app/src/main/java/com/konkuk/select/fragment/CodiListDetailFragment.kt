package com.konkuk.select.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.konkuk.select.R
import com.konkuk.select.activity.DetailCodiActivity
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_codi.*
import kotlinx.android.synthetic.main.fragment_codi_list_detail_flagment.*
import kotlinx.android.synthetic.main.fragment_codi_tag_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiListDetailFragment(val ctx: Context) : Fragment() {

    lateinit var codiListAdapter: CodiListAdapter
    var codiList:ArrayList<Codi> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_codi_list_detail_flagment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolBar()
        setAdapter()
        setClickListener()
        getDataFromBundle()
    }

    fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiTagListFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

    fun setAdapter() {
        codiList_rv.layoutManager = GridLayoutManager(ctx, 2)
        codiListAdapter = CodiListAdapter(ctx, codiList)
        codiList_rv.adapter = codiListAdapter
    }

    fun setClickListener() {
        codiListAdapter.itemClickListener = object : CodiListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: CodiListAdapter.ItemHolder,
                view: View,
                data: Codi,
                position: Int
            ) {
                Toast.makeText(ctx, "${data.id} click", Toast.LENGTH_SHORT).show()
                val intent = Intent(ctx, DetailCodiActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getDataFromBundle(){
        val bundle: Bundle? = arguments
        if(bundle != null) {
            tv_codi_tag.text = bundle.getString("tag")
            val tagId = bundle.getString("tagId").toString()
            getCodiData(tagId)
        }
    }

    private fun getCodiData(tagId:String){
        val tagRef = Fbase.CODITAG_REF.document(tagId)
        Fbase.CODI_REF
            .whereArrayContains("tags", tagRef)
            .whereEqualTo("uid", Fbase.uid)
            .orderBy("date", Query.Direction.ASCENDING) // TODO 최신순으로 정렬하기
            .get().addOnSuccessListener { documents ->
                codiList.clear()
                for (document in documents) {
                    val codiObj = Fbase.getCodi(document)
                    codiList.add(codiObj)
                }
                codiListAdapter.notifyDataSetChanged()
            }
    }
}