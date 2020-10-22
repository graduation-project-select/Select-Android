package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesItemAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_detail_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*

class DetailCodiActivity : AppCompatActivity() {

    lateinit var codiId:String
    lateinit var codiObj: Codi
    lateinit var clothesItemAdapter: ClothesItemAdapter
    var codiItemList = ArrayList<Clothes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_codi)
        setToolBar()
        setAdapter()
        setClickListener()
        getDataFromIntent()
    }

    fun setToolBar() {
        toolbar.title_tv.text = "코디 상세보기"
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.setImageResource(R.drawable.x)
        toolbar.left_iv.setOnClickListener { finish() }
        toolbar.right_iv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    fun setAdapter() {
        codiItem_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        clothesItemAdapter = ClothesItemAdapter(this, codiItemList)
        codiItem_rv.adapter = clothesItemAdapter
    }

    fun setClickListener() {
        clothesItemAdapter.itemClickListener = object : ClothesItemAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: ClothesItemAdapter.ItemHolder,
                view: View,
                data: Clothes,
                position: Int
            ) {
                val intent = Intent(this@DetailCodiActivity, DetailClothesActivity::class.java)
                intent.putExtra("clothesObj", data)
                startActivity(intent)
            }
        }
    }

    private fun getDataFromIntent() {
        intent.getStringExtra("codiId")?.let {
            codiId = it
            Fbase.CODI_REF.document(codiId)
                .get().addOnSuccessListener {document ->
                    codiObj  = Fbase.getCodi(document)
                    initView(codiObj.tags, codiObj.imgUri)
                    getClothesListById(codiObj.itemsIds)
                }
        }
    }

    fun initView(tags:ArrayList<DocumentReference>,imgUri:String){
        var tagString = ""
        for(tag in tags){
            tag.get().addOnSuccessListener {
                tagString += it.get("name")
                codiTag.text = tagString
            }
        }

        Glide.with(this)
            .load(imgUri)
            .into(codiDetailImg)
    }

    // TODO firebase에서 clothes 객체 배열은 가져올수 없나
    fun getClothesListById(itemsIds:ArrayList<String>){
        for(id in itemsIds){
            codiItemList.clear()
            Fbase.CLOTHES_REF.document(id)
                .get().addOnSuccessListener {
                    val clothesObj = Fbase.getClothes(it)
                     codiItemList.add(clothesObj)
                    clothesItemAdapter.notifyDataSetChanged()
                }
        }
    }

}