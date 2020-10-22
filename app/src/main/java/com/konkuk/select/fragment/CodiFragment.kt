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

class CodiFragment : Fragment() {

    private lateinit var codiMainListAdapter:CodiMainListAdapter
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
        settingAdapter()
        getUserCodiTagList()
    }

    private fun settingAdapter(){
        rv_codiList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        codiMainListAdapter = CodiMainListAdapter(myTagList)
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
