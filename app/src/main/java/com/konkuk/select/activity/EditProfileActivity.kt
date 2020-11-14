package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.select.R
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.Fbase.USERS_REF
import com.konkuk.select.storage.SharedPrefManager
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.toolbar.view.*

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setToolbar()
        getUserInfo()
        logout_button.setOnClickListener {
            signOut()
        }
    }

    fun signOut(){
        Fbase.auth.signOut()
        SharedPrefManager.getInstance(this).clear()
        startActivity(Intent(this, LoginActivty::class.java))
    }

    fun setToolbar(){
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.title_tv.text = "프로필 편집"
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "완료"
        toolbar.right_tv.setOnClickListener {
            var name:String? = null
            var gender:String? = null
            var born:Int = -1
            var tag1:String? = null
            var tag2:String? = null

            et_nickname.text?.let {
                name = it.toString()
            }
            et_gender.text?.let {
                gender = it.toString()
            }
            et_born.text?.let{
                born = it.toString().toInt()
            }
            et_tag1.text?.let {
                tag1 = it.toString()
            }
            et_tag2.text?.let {
                tag2 = it.toString()
            }

            if(name != null && gender != null && born != -1 && tag1 != null && tag2 != null){
                updateUserInfo(name!!, gender!!, born, tag1!!, tag2!!)
                finish()
            }else{
                Toast.makeText(this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getUserInfo(){
        Fbase.uid?.let {
            USERS_REF.document(it).get().addOnSuccessListener {userObj ->
                if(userObj.exists()){
                    userObj.get("name")?.let {
                        et_nickname.setText(it.toString())
                    }
                    userObj.get("gender")?.let{
                        et_gender.setText(it.toString())
                    }
                    userObj.get("born")?.let{
                        et_born.setText(it.toString())
                    }
                    userObj.get("tag1")?.let{
                        et_tag1.setText(it.toString())
                    }
                    userObj.get("tag2")?.let{
                        et_tag2.setText(it.toString())
                    }
                }
            }
        }
    }

    fun updateUserInfo(name:String, gender:String, born:Int, tag1:String, tag2:String){

        Fbase.uid?.let {
            USERS_REF.document(it)
                    .update(
                        mapOf(
                            "name" to name,
                            "gender" to gender,
                            "born" to born,
                            "tag1" to tag1,
                            "tag2" to tag2
                        )
                    )
        }
    }

}
