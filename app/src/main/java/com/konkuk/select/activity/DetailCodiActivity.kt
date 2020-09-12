package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothItemAdapter
import com.konkuk.select.model.Clothes
import kotlinx.android.synthetic.main.activity_detail_codi.*

class DetailCodiActivity : AppCompatActivity() {
    lateinit var clothItemAdapter: ClothItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_codi)

        // ToolBar 변경하는 코드
        val includeToolBar : View = findViewById(R.id.toolbar)
        val leftBtn : ImageView = includeToolBar.findViewById(R.id.left_iv)
        val title : TextView = includeToolBar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = includeToolBar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)
        title.text = "코디 상세보기"
        rightBtn.setImageResource(0)

        var codiItemList = ArrayList<Clothes>()
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));
        codiItemList.add(Clothes("111", "top", "0"));


        codiItem_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        clothItemAdapter = ClothItemAdapter(codiItemList)
        codiItem_rv.adapter = clothItemAdapter
    }
}