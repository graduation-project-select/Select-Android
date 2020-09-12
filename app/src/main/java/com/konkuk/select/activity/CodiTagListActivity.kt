package com.konkuk.select.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagListAdapter
import com.konkuk.select.model.CodiTag
import kotlinx.android.synthetic.main.activity_codi_tag_list.*


class CodiTagListActivity : AppCompatActivity() {

    lateinit var codiTagAdapter: CodiTagListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codi_tag_list)

        // ToolBar 변경하는 코드
        val includeToolBar : View = findViewById(R.id.toolbar)
        val leftBtn : ImageView = includeToolBar.findViewById(R.id.left_iv)
        val title : TextView = includeToolBar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = includeToolBar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)


        var codiTagList = ArrayList<CodiTag>()
        codiTagList.add(CodiTag("111", "#데이트룩"))
        codiTagList.add(CodiTag("222", "#오피스룩"))
        codiTagList.add(CodiTag("333", "#캠퍼스룩"))

        codiTag_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        codiTagAdapter = CodiTagListAdapter(codiTagList)
        codiTag_rv.adapter = codiTagAdapter

    }
}