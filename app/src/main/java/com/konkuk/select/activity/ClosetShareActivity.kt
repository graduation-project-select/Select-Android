package com.konkuk.select.activity

import android.content.Intent
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

    var closetId = "2PEmGmU41ZZhvRENoCr0"
    var uid = "bMYknEYE6RPK4JEOdiBrTc7vAs33"    // 고서영

    var myUid = "enmKDWEYDxgE2GxTmTSd5BFVHyp1"  // 최서희

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_share)
        settingToolBar()
        initViews(closetId)
        settingFragment(closetId, uid)
        settingOnClickListener()
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

    private fun initViews(closetId: String) {
        Fbase.CLOSETS_REF.document(closetId)
            .get().addOnSuccessListener {
                val closetObj = Fbase.getCloset(it)
                tv_closet_name.text = closetObj.name
            }
        Fbase.USERS_REF.document(uid).get().addOnSuccessListener {
            val userName = it["name"].toString()
            tv_nickname1.text = userName
            tv_nickname2.text = userName
        }
    }

    private fun settingFragment(closetId: String, uid: String) {
        supportFragmentManager.beginTransaction().replace(
            R.id.closet_container,
            ClothesListFragment.newInstance(closetId, uid)
        ).commit()
    }

    private fun settingOnClickListener() {
        addCodiBtn.setOnClickListener {
            var nextIntent = Intent(this, AddCodiActivity::class.java)
            nextIntent.putExtra("isSahring", true)
            nextIntent.putExtra("closetId", closetId)
            nextIntent.putExtra("currentUid", uid)
            startActivity(nextIntent)
        }
    }

}
