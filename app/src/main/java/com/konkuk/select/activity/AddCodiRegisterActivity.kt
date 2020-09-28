package com.konkuk.select.activity

import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagCheckboxListAdapter
import com.konkuk.select.model.CodiTag
import kotlinx.android.synthetic.main.activity_add_codi_register.*
import kotlinx.android.synthetic.main.toolbar.view.*


class AddCodiRegisterActivity : AppCompatActivity() {
    lateinit var codiTagCheckboxListAdapter: CodiTagCheckboxListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi_register)
        setToolBar()
        setCodiImage()
        settingAdapter()


        /* 해야할일
        그리드 리사이클러뷰 화면 가로길이 받아와서 동적 열 구성
        올리기 눌렀 때 디비에 저장
        -> 아이디(랜덤수), 이미지, 태그, 공개/비공개
        */

    }

    fun setToolBar(){
        toolbar.title_tv.text = getString(R.string.activity_title_addCodiRegister)
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "올리기"

        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_tv.setOnClickListener {
            finish()
            // 방금 올린 코디 상세 페이지로 이동
        }
    }

    fun setCodiImage() {
        val imgByte = intent.getByteArrayExtra("codiImage")
        val imgDecoding = BitmapFactory.decodeByteArray(imgByte, 0, imgByte!!.size)
        codiDetailImg.setImageBitmap(imgDecoding)
    }

    fun settingAdapter() {
        var codiTagList = ArrayList<CodiTag>()
        codiTagList.add(CodiTag("111", "#데이트룩"))
        codiTagList.add(CodiTag("222", "#오피스룩"))
        codiTagList.add(CodiTag("333", "#캠퍼스룩"))
        codiTagList.add(CodiTag("444", "#헬스장갈때"))
        codiTagList.add(CodiTag("555", "#파티룩"))
        codiTagList.add(CodiTag("777", "#바람핀전남친결혼식갈때"))

        codiTag_rv.layoutManager = GridLayoutManager(this, 2)
        codiTagCheckboxListAdapter = CodiTagCheckboxListAdapter(this, codiTagList)
        codiTag_rv.adapter = codiTagCheckboxListAdapter

        val myDisplay = applicationContext.resources.displayMetrics
        val deviceWidth = myDisplay.widthPixels
        // 코디 태그 뷰마다 길이 가져와서 계산하여 화면 width보다 크면 다음줄 작으면 한 줄에 출력
    }
}