package com.konkuk.select.storage

import android.content.Context
import android.content.Intent
import com.konkuk.select.activity.LoginActivty
import com.konkuk.select.activity.MainActivity

class SharedPrefManager private constructor(private val mCtx: Context) {
    val uid: String?
        get(){
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("uid", null)
        }

    fun saveUid(uid: String){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("uid", uid)
        editor.apply()
    }

    // 로그아웃
    fun clear(){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // 로그인 되어 있지 않으면 메인 화면의 로그인 프레그먼트으로 간다.
    fun checkLogin(context: Context): Boolean? {
        if(uid != "" && uid != null){
            return true
        }else{
            val intent = Intent(context, LoginActivty::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
        return false
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shred_preff"
        private  var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx:Context): SharedPrefManager {
            if(mInstance == null){
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }

    }




}