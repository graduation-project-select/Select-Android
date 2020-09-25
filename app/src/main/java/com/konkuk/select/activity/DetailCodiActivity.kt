package com.konkuk.select.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesItemAdapter
import com.konkuk.select.model.Clothes
import kotlinx.android.synthetic.main.activity_detail_clothes.*
import kotlinx.android.synthetic.main.activity_detail_codi.*
import kotlinx.android.synthetic.main.activity_detail_codi.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class DetailCodiActivity : AppCompatActivity() {
    lateinit var clothesItemAdapter: ClothesItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_codi)

        setToolBar()
        setAdapter()
        setClickListener()
    }

    fun setToolBar() {
        toolbar.title_tv.text = "코디 상세보기"
        toolbar.left_iv.setImageResource(0)
        toolbar.right_iv.setImageResource(R.drawable.x)

        toolbar.right_iv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    fun setAdapter() {
        var codiItemList = ArrayList<Clothes>()
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));

        codiItem_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        clothesItemAdapter = ClothesItemAdapter(codiItemList)
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
                startActivity(intent)
            }
        }
    }
}