package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.konkuk.select.R
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.CLOSETS_REF
import com.konkuk.select.network.Fbase.CLOTHES_REF
import kotlinx.android.synthetic.main.activity_edit_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*

private const val CLOSET_ID_MESSAGE = "closetId"
private const val CLOSET_NAME_MESSAGE = "closetName"

class EditClosetActivity : AppCompatActivity() {

    var closetId:String = ""
    var closetName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_closet)
        setToolbar()
        getDataFromIntent()
        setOnClickListener()
    }

    fun setToolbar(){
        toolbar.title_tv.text = "옷장 편집"
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "완료"
        toolbar.right_tv.setOnClickListener {
            // 편집 db에 저장
            CLOSETS_REF.document(closetId)
                .update("name", closetName_et.text.toString())
                .addOnSuccessListener {
//                    Toast.makeText(this, "변경 완료", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    fun getDataFromIntent(){
        if(intent.hasExtra(CLOSET_ID_MESSAGE) && intent.hasExtra(CLOSET_NAME_MESSAGE)){
            intent.getStringExtra(CLOSET_ID_MESSAGE)?.let{ closetId = it }
            intent.getStringExtra(CLOSET_NAME_MESSAGE)?.let{ closetName = it }
        }else{
            Log.e("EditClosetActivity", "error in getting data from intent")
            finish()
        }
//        Toast.makeText(this, "closetId: $closetId, closetName: $closetName", Toast.LENGTH_SHORT).show()
        closetName_et.setText(closetName)
    }

    fun setOnClickListener(){
        deleteButton.setOnClickListener {
            // 옷들에서 옷장 배열에 해당 id 지워준 후
            CLOTHES_REF.whereEqualTo("uid", Fbase.uid)
                .whereArrayContains("closet", closetId)
                .get().addOnSuccessListener {
                    it.documents.forEach {doc ->
                        removeClosetIdFromClothes(doc.id)
                    }
                }
            // 옷장 삭제
            CLOSETS_REF.document(closetId)
                .delete()
                .addOnSuccessListener {
//                    Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    fun removeClosetIdFromClothes(clothesId:String){
        CLOTHES_REF.document(clothesId).update("closet", FieldValue.arrayRemove(closetId))
    }

    // TODO 옷 목록 편집 가능하게 해야 함

}
