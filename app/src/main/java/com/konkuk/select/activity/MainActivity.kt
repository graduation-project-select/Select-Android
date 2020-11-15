package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.select.R
import com.konkuk.select.fragment.*
import com.konkuk.select.network.Fbase
import com.konkuk.select.storage.SharedPrefManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

private const val CLOSET_ID_MESSAGE = "closetId"
private const val CLOSET_NAME_MESSAGE = "closetName"
private const val SIGN_OUT_MESSAGE = "signOut"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            initClosetMenuFragment(null, null)
        }

        getDataFromIntent()

        navigationView.setOnNavigationItemSelectedListener {
            initFragment(it.itemId)
        }

        add_iv.setOnClickListener {
            showBottomSheetMainBtnDialogFragment()
        }

    }

    private fun getDataFromIntent(){
        // 옷장 선택한 경우
        if(intent.hasExtra(CLOSET_ID_MESSAGE) && intent.hasExtra(CLOSET_NAME_MESSAGE)){
            val closetId = intent.getStringExtra(CLOSET_ID_MESSAGE)
            val closetName = intent.getStringExtra(CLOSET_NAME_MESSAGE)
            initClosetMenuFragment(closetId, closetName)
        }
    }

    private fun initFragment(menuItem: Int):Boolean {
        when (menuItem) {
            R.id.closetItem -> {
                initClosetMenuFragment(null,null)
                return true
            }
            R.id.codiItem -> {
                initCodiMenuFragment()
                return true
            }
            R.id.communityItem -> {
                initCommunityFragment()
                return true
            }
            R.id.MyPageItem -> {
                initMyPageFragment()
                return true
            }
        }
        return false
    }

    private fun initClosetMenuFragment(closetId:String?, closetName:String?){
        initClosetMenuToolbar()
        var fragment = ClosetFragment()
        if(closetId != null && closetName != null){
            fragment = ClosetFragment.newInstance(closetId, closetName)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun initClosetMenuToolbar(){
        toolbar.left_iv.visibility = View.VISIBLE
        toolbar.left_iv.setImageResource(R.drawable.closet_btn)
        toolbar.left_iv.setOnClickListener {
            startActivity(Intent(this, ClosetListActivity::class.java))
        }
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            moveToNotification()
        }
    }

    private fun initCodiMenuFragment(){
        initCodiMenuToolbar()
        val fragment = CodiFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun initCodiMenuToolbar(){
        toolbar.left_iv.visibility = View.VISIBLE
        toolbar.left_iv.setImageResource(R.drawable.hashtag)
        toolbar.left_iv.setOnClickListener {
            startActivity(Intent(this, CodiTagListActivity::class.java))
        }
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            moveToNotification()
        }
    }

    private fun initCommunityFragment(){
        initCommunityToolbar()
        val fragment = CommunityFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun initCommunityToolbar(){
        toolbar.left_iv.visibility = View.INVISIBLE
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            moveToNotification()
        }
    }

    private fun initMyPageFragment(){
        initMyPageToolbar()
        val fragment = MyPageFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun initMyPageToolbar() {
        toolbar.left_iv.visibility = View.INVISIBLE
        toolbar.title_tv.text = "셀렉"
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.setting_btn)
        toolbar.right_iv.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }



    private fun moveToNotification(){
        var nextIntent = Intent(this, NotificationListActivity::class.java)
        startActivity(nextIntent)
    }

    private fun showBottomSheetMainBtnDialogFragment() {
        val bottomSheetFragment = BottomSheetMainBtnDialog(this)
        supportFragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.getTag()) }
    }

}
