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
import kotlinx.android.synthetic.main.activity_detail_codi.*

class DetailCodiActivity : AppCompatActivity() {
    lateinit var clothesItemAdapter: ClothesItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_codi)

        // ToolBar 변경하는 코드
        val includeToolBar : View = findViewById(R.id.toolbar)
        val leftBtn : ImageView = includeToolBar.findViewById(R.id.left_iv)
        val title : TextView = includeToolBar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = includeToolBar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(0)
        title.text = "코디 상세보기"
        rightBtn.setImageResource(R.drawable.x)

        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var codiItemList = ArrayList<Clothes>()
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));


        codiItem_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        clothesItemAdapter = ClothesItemAdapter(codiItemList)
        codiItem_rv.adapter = clothesItemAdapter
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