package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClothesItemAdapter
import com.konkuk.select.adpater.CodiItemAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi
import kotlinx.android.synthetic.main.activity_detail_clothes.*
import kotlinx.android.synthetic.main.toolbar.view.*

class DetailClothesActivity : AppCompatActivity() {
    lateinit var codiItemAdapter: CodiItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_clothes)

        setToolBar()
        setAdapter()
        setClickListener()
    }

    fun setToolBar() {
        toolbar.title_tv.text = "옷 상세보기"
        toolbar.left_iv.setImageResource(0)
        toolbar.right_iv.setImageResource(R.drawable.x)

        toolbar.right_iv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    fun setAdapter() {
        var codiList = ArrayList<Codi>()
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))

        codi_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        codiItemAdapter = CodiItemAdapter(codiList)
        codi_rv.adapter = codiItemAdapter
    }

    fun setClickListener() {
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
}