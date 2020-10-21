package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.select.R
import com.konkuk.select.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

private const val CLOSET_ID_MESSAGE = "closetId"
private const val CLOSET_NAME_MESSAGE = "closetName"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            initClosetMenuFragment(null, null)
        }

        if(intent.hasExtra(CLOSET_ID_MESSAGE) && intent.hasExtra(CLOSET_NAME_MESSAGE)){
            val closetId = intent.getStringExtra(CLOSET_ID_MESSAGE)
            val closetName = intent.getStringExtra(CLOSET_NAME_MESSAGE)
            initClosetMenuFragment(closetId, closetName)
        }

        navigationView.setOnNavigationItemSelectedListener {
            initFragment(it.itemId)
        }

        add_iv.setOnClickListener {
            showBottomSheetMainBtnDialogFragment()
        }

    }

    private fun initFragment(menuItem: Int):Boolean {
        when (menuItem) {
            R.id.closetItem -> {
                initClosetMenuFragment(null,null)
                return true
            }
            R.id.codiItem -> {
                val fragment = CodiFragment(this)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
                return true
            }
            R.id.communityItem -> {
                val fragment = CommunityFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
                return true
            }
            R.id.MyPageItem -> {
                val fragment = MyPageFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
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
        toolbar.left_iv.setImageResource(R.drawable.closet_btn)
        toolbar.left_iv.setOnClickListener {
            startActivity(Intent(this, ClosetListActivity::class.java))
        }
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            Toast.makeText(this, "알림 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheetMainBtnDialogFragment() {
        val bottomSheetFragment = BottomSheetMainBtnDialog(this)
        supportFragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.getTag()) }
    }

}
