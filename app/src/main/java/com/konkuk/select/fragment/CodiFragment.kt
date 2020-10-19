package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiMainListAdapter
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiFragment(val ctx: Context) : Fragment() {

    lateinit var codiMainListAdapter:CodiMainListAdapter
    var myTagList: ArrayList<DocumentReference> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_codi, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingToolBar()
        settingAdapter()
        getUserCodiTagList()
    }

    private fun settingToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.hashtag)
        toolbar.left_iv.setOnClickListener {
            Toast.makeText(ctx, "코디 태그 메뉴", Toast.LENGTH_SHORT).show()
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiTagListFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

    private fun settingAdapter(){
        rv_codiList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        codiMainListAdapter = CodiMainListAdapter(ctx, myTagList)
        rv_codiList.adapter = codiMainListAdapter
    }


    private fun getUserCodiTagList() {
        Fbase.uid?.let {
            Fbase.USERS_REF.document(it).get().addOnSuccessListener {
                val codiTagList = it.get("codiTagList") as ArrayList<DocumentReference>
                myTagList.clear()
                for (tag in codiTagList) {
                    myTagList.add(tag)
                }
                codiMainListAdapter.notifyDataSetChanged()
            }
        }
    }

}
