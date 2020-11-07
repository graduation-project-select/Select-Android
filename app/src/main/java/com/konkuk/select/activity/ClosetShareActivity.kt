package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.fragment.ClothesListFragment
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_closet_share.*
import kotlinx.android.synthetic.main.activity_detail_clothes.toolbar
import kotlinx.android.synthetic.main.row_bottom_sheet_closet_list.*
import kotlinx.android.synthetic.main.toolbar.view.*

const val NOTI_TYPE = "CLOSET_SHARE"

class ClosetShareActivity : AppCompatActivity() {

    var closetId = "BxsTo6sPSG8PpBRF2mcu"           // 링크에서 전달받은 값
    var ownerUid = "bMYknEYE6RPK4JEOdiBrTc7vAs33"    // 링크에서 전달받은 값 ex) 고서영 (옷장 주인)
    var senderUid = "Np4U7KQ3nNRVCDUkMHeWex6Atrz1"  // 현재 사용자 (Fbase.uid)
    // ex) 사연진 (KEX3pWvkmKZuMD3aipyP0HzORmE2), 최서희(Np4U7KQ3nNRVCDUkMHeWex6Atrz1)

    var codiIds = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_share)
        settingToolBar()
        initViews(closetId, ownerUid)
        settingFragment(closetId, ownerUid)
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
            var nextIntent = Intent(this, CodiSuggestionListActivity::class.java)
            nextIntent.putExtra("isSharing", true)
            nextIntent.putExtra("closetId", closetId)
            nextIntent.putExtra("ownerUid", ownerUid)
            nextIntent.putExtra("senderUid", senderUid)
            startActivity(nextIntent)
        }
        toolbar.right_tv.setOnClickListener {
            Toast.makeText(this, "코디추천 완료", Toast.LENGTH_SHORT).show()
            createAlarm()
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
        }
//        toolbar.right_iv.setOnClickListener {
//            Toast.makeText(this, "코디 추천 종료", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun initViews(closetId: String, uid: String) {
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
            ClothesListFragment.newInstance(closetId, uid, isSharing = true)
        ).commit()
    }

    private fun settingOnClickListener() {
        // 코디 추가
        addCodiBtn.setOnClickListener {
            var nextIntent = Intent(this, AddCodiActivity::class.java)
            nextIntent.putExtra("isSharing", true)
            nextIntent.putExtra("closetId", closetId)
            nextIntent.putExtra("ownerUid", ownerUid)
            nextIntent.putExtra("senderUid", senderUid)
            startActivity(nextIntent)
        }
    }


    private fun createAlarm(){
        getCodiSuggestion()
    }

    data class CodiSugNotiRequest(
        val codiIds:ArrayList<String>,
        val closetId:String,
        val ownerUid:String,
        val senderUid:String,
        val timestamp: Timestamp
    )

    data class NotificationRequest(
        val uid: String,
        val type:String,
        val notiRef:DocumentReference
    )

    private fun getCodiSuggestion(){
        Fbase.CODI_SUGGESTION_REF
            .whereEqualTo("closetId" , closetId)
            .whereEqualTo("ownerUid" , ownerUid)
            .whereEqualTo("senderUid" , senderUid)
            .get().addOnSuccessListener {
                codiIds.clear()
                for(document in it){
                    codiIds.add(document.id)
                }
                createCodiSuggestionNotification()
            }
    }

    private fun createCodiSuggestionNotification(){
        val codiSugNotiReqObj = CodiSugNotiRequest(codiIds,closetId,ownerUid,senderUid, Timestamp.now())
        Fbase.CODISUG_NOTI_REF.add(codiSugNotiReqObj)
            .addOnSuccessListener {
                createNotification(it)
            }
    }

    private fun createNotification(codiSugNotiRef:DocumentReference){
        val NotificationReqObj = NotificationRequest(ownerUid, NOTI_TYPE, codiSugNotiRef)
        Fbase.NOTIFICATION_REF.add(NotificationReqObj)
    }
}
