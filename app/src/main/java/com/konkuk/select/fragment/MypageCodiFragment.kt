package com.konkuk.select.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference

import com.konkuk.select.R
import com.konkuk.select.adpater.CodiMainListAdapter
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_mypage_codi.*


class MypageCodiFragment : Fragment() {
    private lateinit var codiMainListAdapter: CodiMainListAdapter
    var myTagList: ArrayList<DocumentReference> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage_codi, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_codiList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        codiMainListAdapter = CodiMainListAdapter(myTagList, true)
        rv_codiList.adapter = codiMainListAdapter

        getUserCodiTagList()
    }

    private fun getUserCodiTagList() {
        Fbase.uid?.let {
            Fbase.USERS_REF.document(it).get().addOnSuccessListener {
                if(it.get("codiTagList") != null){
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

}
