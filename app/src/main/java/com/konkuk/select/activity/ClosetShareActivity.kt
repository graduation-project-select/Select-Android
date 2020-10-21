package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.konkuk.select.R
import com.konkuk.select.model.Closet
import kotlinx.android.synthetic.main.activity_detail_clothes.*
import kotlinx.android.synthetic.main.toolbar.view.*

class ClosetShareActivity : AppCompatActivity() {

    val closetObj = Closet("QFLqfUSDCRH9PanZNEQi","sysy", 5,"", "xeaW91ehsQMNcbqdSTVFCh5EkRk2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_share)
        settingToolBar()
    }

    private fun settingToolBar() {
        toolbar.title_tv.text = "옷장 공유"
        toolbar.left_iv.setImageResource(0)
        toolbar.right_iv.setImageResource(R.drawable.x)

        toolbar.right_iv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}
