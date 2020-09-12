package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.konkuk.select.R
import kotlinx.android.synthetic.main.activity_add_cloth.*

class AddClothActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cloth)

        // ToolBar 변경하는 코드
        val leftBtn : ImageView = toolbar.findViewById(R.id.left_iv)
        val title : TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)
        title.text = "옷 추가하기"
        rightBtn.setImageResource(0)

    }
}