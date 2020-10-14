package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.konkuk.select.R
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_add_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.util.*

class AddClosetActivity : AppCompatActivity() {

    val TAG="AddCloset"
    var closetId:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_closet)
        settingToolBar()
        settingOnClickListener()
    }

    private fun settingToolBar(){
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.title_tv.text = "옷장 추가하기"
        toolbar.right_tv.visibility = View.INVISIBLE
        toolbar.right_iv.visibility = View.GONE

        toolbar.left_iv.setOnClickListener {
            finish()
        }
    }

    private fun settingOnClickListener(){
        addBtn.setOnClickListener{
            Toast.makeText(this, "옷장 추가", Toast.LENGTH_SHORT).show()
            createNewCloset(et_name.text.toString())

        }
    }

    private fun createNewCloset(name:String) {
        // Create a new user with a first and last name
        val closet = hashMapOf(
            "uid" to Fbase.uid,
            "name" to name,
            "count" to 0
        )
        Log.d(TAG, "insertCloset: ${closet}")

        // Add a new document with a generated ID
        Fbase.db.collection("closets")
            .add(closet)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                finish()
                closetId = documentReference.id
                Log.d("closetId", "보낸거: "+closetId!!)
                gotoNextStep(closetId!!)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                closetId = null
            }
    }

    private fun gotoNextStep(closetId: String) {
        closetId.let {
            var nextIntent = Intent(this, ClothesCheckListActivity::class.java)
            nextIntent.putExtra("closetId", it)
            startActivity(nextIntent)
        }
        finish()
    }


}
