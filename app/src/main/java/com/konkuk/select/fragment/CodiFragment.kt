package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.konkuk.select.R
import kotlinx.android.synthetic.main.fragment_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiFragment(val ctx: Context) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codi, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingOnClickListener()
    }


    fun settingOnClickListener() {
        toolbar.left_iv.setOnClickListener {
            Toast.makeText(ctx, "코디 태그 메뉴", Toast.LENGTH_SHORT).show()
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiListDetailFlagment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()

        }
    }

}
