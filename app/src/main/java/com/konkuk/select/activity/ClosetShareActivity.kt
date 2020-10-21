package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.konkuk.select.R
import com.konkuk.select.fragment.ClosetFragment
import com.konkuk.select.fragment.ClothesListFragment
import com.konkuk.select.model.Closet
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_closet_share.*
import kotlinx.android.synthetic.main.activity_detail_clothes.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class ClosetShareActivity : AppCompatActivity() {

    val closetObj = Closet("AD1fQXH3A6zeuh2hkSTh", "My Closet", 3, "", "bMYknEYE6RPK4JEOdiBrTc7vAs33")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_share)
        settingToolBar()
        initViews()
        settingFragment()
    }

    private fun settingToolBar() {
        toolbar.title_tv.text = "옷장 공유"
        toolbar.left_iv.setImageResource(R.drawable.closet_btn)
        toolbar.right_tv.text = "완료"
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_iv.visibility = View.GONE
//        toolbar.right_iv.setImageResource(R.drawable.x)

        toolbar.left_iv.setOnClickListener {
            Toast.makeText(this, "코디 리스트 보기", Toast.LENGTH_SHORT).show()
        }
        toolbar.right_tv.setOnClickListener {
            Toast.makeText(this, "코디추천 완료", Toast.LENGTH_SHORT).show()
        }
//        toolbar.right_iv.setOnClickListener {
//            Toast.makeText(this, "코디 추천 종료", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun initViews() {
        Fbase.USERS_REF.document(closetObj.uid).get().addOnSuccessListener {
            val userName = it["name"].toString()
            tv_nickname1.text = userName
            tv_nickname2.text = userName
            tv_closet_name.text = closetObj.name
        }
    }

    private fun settingFragment() {
        // TODO 여기로 closetId와 uid를 보내야겠다 (아니면 옷장으로 필터링시->uid는 필터링할 필요가 없지 않나?)
        val fragment =
            supportFragmentManager.beginTransaction().replace(
                R.id.closet_container,
                ClothesListFragment.newInstance(closetObj.id, closetObj.uid)
            ).commit()
    }

}
