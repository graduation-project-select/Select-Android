package com.konkuk.select.activity

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.select.R
import com.konkuk.select.fragment.ClosetFragment
import com.konkuk.select.fragment.CodiFragment
import com.konkuk.select.fragment.CommunityFragment
import com.konkuk.select.fragment.MyPageFragment
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {

    private var currentFragment = 1 // 뒤로가기 위해 필요

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val toolbar = findViewById(R.id.toolbar) as Toolbar
//        toolbar.setTitle(R.string.app_name)
//        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            val fragment = ClosetFragment(this)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                .commit()
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.closetItem -> {
                    val fragment = ClosetFragment(this)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.codiItem -> {
                    val fragment = CodiFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.communityItem -> {
                    val fragment = CommunityFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.MyPageItem -> {
                    val fragment = MyPageFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener false
        }

        add_iv.setOnClickListener {
            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
        }

    }

}
