package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.konkuk.select.R

class DetailCodiActivity : AppCompatActivity() {
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
    }
}