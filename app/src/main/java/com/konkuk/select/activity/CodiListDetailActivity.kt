package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.Query
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_codi_tag_list.toolbar
import kotlinx.android.synthetic.main.activity_codi_list_detail.*
import kotlinx.android.synthetic.main.toolbar.view.*

private const val TAG_ID_MESSAGE = "tagId"
private const val TAG_NAME_MESSAGE = "tagName"

class CodiListDetailActivity : AppCompatActivity() {

    lateinit var codiListAdapter: CodiListAdapter
    var codiList:ArrayList<Codi> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codi_list_detail)
        setToolBar()
        setAdapter()
        setClickListener()
        getDataFromIntent()
    }

    fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_iv.visibility = View.INVISIBLE
    }

    fun setAdapter() {
        codiList_rv.layoutManager = GridLayoutManager(this, 2)
        codiListAdapter = CodiListAdapter(this, codiList)
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
                val intent = Intent(this@CodiListDetailActivity, DetailCodiActivity::class.java)
                intent.putExtra("codiId", data.id)
                startActivity(intent)
            }
        }
    }

    private fun getDataFromIntent(){
        if(intent.hasExtra(TAG_ID_MESSAGE) && intent.hasExtra(TAG_NAME_MESSAGE)){
            tv_codi_tag.text = intent.getStringExtra(TAG_NAME_MESSAGE)
            val tagId = intent.getStringExtra(TAG_ID_MESSAGE).toString()
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