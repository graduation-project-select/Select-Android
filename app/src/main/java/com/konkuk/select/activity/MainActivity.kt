package com.konkuk.select.activity

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.select.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.konkuk.select.fragment.*


class MainActivity : AppCompatActivity() {

    private var currentFragment = 1 // 뒤로가기 위해 필요
//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        auth = Firebase.auth
//        Toast.makeText(this, auth.uid, Toast.LENGTH_SHORT).show()

        if (savedInstanceState == null) {
            val fragment = ClosetFragment(this)
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                .commit()
        }

        navigationView.setOnNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.closetItem -> {
                    val fragment = ClosetFragment(this)
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.codiItem -> {
                    val fragment = CodiFragment(this)
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.communityItem -> {
                    val fragment = CommunityFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.MyPageItem -> {
                    val fragment = MyPageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener false
        }

        add_iv.setOnClickListener {
            showBottomSheetMainBtnDialogFragment()
        }

    }

    fun showBottomSheetMainBtnDialogFragment() {
        val bottomSheetFragment = BottomSheetMainBtnDialog(this)
        supportFragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.getTag()) }
    }

}
