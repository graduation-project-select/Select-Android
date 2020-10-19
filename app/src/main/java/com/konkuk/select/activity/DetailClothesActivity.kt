package com.konkuk.select.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesItemAdapter
import com.konkuk.select.adpater.CodiItemAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi
import kotlinx.android.synthetic.main.activity_add_clothes.*
import kotlinx.android.synthetic.main.activity_detail_clothes.*
import kotlinx.android.synthetic.main.activity_detail_clothes.categorySub_tv
import kotlinx.android.synthetic.main.activity_detail_clothes.category_tv
import kotlinx.android.synthetic.main.activity_detail_clothes.colorCircle
import kotlinx.android.synthetic.main.activity_detail_clothes.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

// TODO 옷 편집, 삭제
class DetailClothesActivity : AppCompatActivity() {
    lateinit var codiItemAdapter: CodiItemAdapter

    lateinit var clothesObj: Clothes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_clothes)
        settingToolBar()
        settingAdapter()
        settingClickListener()
        getDataFromIntent()
    }

    private fun settingToolBar() {
        toolbar.title_tv.text = "옷 상세보기"
        toolbar.left_iv.setImageResource(0)
        toolbar.right_iv.setImageResource(R.drawable.x)

        toolbar.right_iv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun settingAdapter() {
        var codiList = ArrayList<Codi>()
//        codiList.add(Codi("111", "#데이트룩", "0", true))
//        codiList.add(Codi("111", "#데이트룩", "0", true))
//        codiList.add(Codi("111", "#데이트룩", "0", true))
//        codiList.add(Codi("111", "#데이트룩", "0", true))
//        codiList.add(Codi("111", "#데이트룩", "0", true))

        codi_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        codiItemAdapter = CodiItemAdapter(codiList)
        codi_rv.adapter = codiItemAdapter
    }

    private fun settingClickListener() {
        codiItemAdapter.itemClickListener = object : CodiItemAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: CodiItemAdapter.ItemHolder,
                view: View,
                data: Codi,
                position: Int
            ) {
                val intent = Intent(this@DetailClothesActivity, DetailCodiActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun getDataFromIntent() {
        clothesObj = intent.getSerializableExtra("clothesObj") as Clothes
        Log.d("TAG", clothesObj.toString())
        initView(clothesObj.category, clothesObj.subCategory, clothesObj.color, clothesObj.season, clothesObj.imgUri)
    }

    private fun initView(
        category: String,
        subCategory: String,
        clothesRGB: ArrayList<Int>,
        season: ArrayList<Boolean>,
        imgUri: String
    ) {
        category_tv.text = category
        categorySub_tv.text = subCategory

        val hex = java.lang.String.format(
            "#%02x%02x%02x",
            clothesRGB[0],
            clothesRGB[1],
            clothesRGB[2]
        )
        colorCircle.setBackgroundColor(Color.parseColor(hex))

        val seasonText:ArrayList<String> = arrayListOf("봄", "여름", "가을", "겨울")
        var seasonStr = ""
        for ((i, s) in season.withIndex()) {
            if (s) {
                if(seasonStr != "") seasonStr += ","
                seasonStr += seasonText[i]
            }
            if (i == 3 && seasonStr != "") seasonStr += " 용"
        }
        if(seasonStr == "") seasonStr = "계절을 등록해주세요"
        season_tv.text = seasonStr

        Glide.with(this)
            .load(imgUri)
            .into(clothDetailImg)
    }

}