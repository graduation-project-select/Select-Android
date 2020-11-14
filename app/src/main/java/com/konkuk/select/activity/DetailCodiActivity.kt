package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesItemAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.CODI_REF
import kotlinx.android.synthetic.main.activity_detail_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        CombiDoCodi.setOnClickListener {
            val intent = Intent(this@DetailCodiActivity, AddCodiActivity::class.java)
            intent.putExtra("combiCodiClothesList", codiItemList)
            startActivity(intent)
        }
        deleteButton.setOnClickListener {
            CODI_REF.document(codiObj.id).delete().addOnSuccessListener {
                Toast.makeText(this, "Codi ${codiObj.id} 삭제",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }

        // Edit
        var builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("날짜를 선택하세요")
        var materialDatePicker = builder.build()
        materialDatePicker.addOnPositiveButtonClickListener {timestamp ->
            val calendarObj = Calendar.getInstance()
            calendarObj.time = Date(timestamp)
            var year = calendarObj.get(Calendar.YEAR)
            var month = calendarObj.get(Calendar.MONTH) + 1
            var date = calendarObj.get(Calendar.DATE)
            lastWearDate.text = "$year.$month.$date"

            CODI_REF.document(codiId)
                .update(mapOf(
                    "timestamp" to timestamp,
                    "year" to year,
                    "month" to month,
                    "date" to date
                )).addOnSuccessListener {
                    Toast.makeText(this, "변경된 날짜: $year.$month.$date", Toast.LENGTH_SHORT).show()
                }
        }
        lastWearDate.setOnClickListener {
            materialDatePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun getDataFromIntent() {
        intent.getStringExtra("codiId")?.let {
            codiId = it
            Fbase.CODI_REF.document(codiId)
                .get().addOnSuccessListener { document ->
                    codiObj  = Fbase.getCodi(document)
                    initView(codiObj.tags, codiObj.imgUri, codiObj.year, codiObj.month, codiObj.date)
                    getClothesListById(codiObj.itemsIds)
                }
        }
    }

    fun initView(tags: ArrayList<DocumentReference>, imgUri: String, year:Int, month:Int, date:Int){
        // 태그
        var tagString = ""
        for(tag in tags){
            tag.get().addOnSuccessListener {
                tagString += "#${it.get("name")} "
                codiTag.text = tagString
            }
        }
        // 이미지
        Glide.with(this)
            .load(imgUri)
            .into(codiDetailImg)
        // 날짜
        lastWearDate.text = "$year.$month.$date"
    }

    fun getClothesListById(itemsIds: ArrayList<String>){
        for(id in itemsIds){
            codiItemList.clear()
            Fbase.CLOTHES_REF.document(id)
                .get().addOnSuccessListener {
                    val clothesObj = Fbase.getClothes(it)
                    if(clothesObj != null){
                        codiItemList.add(clothesObj)
                        clothesItemAdapter.notifyDataSetChanged()
                    }
                }
        }
    }

}