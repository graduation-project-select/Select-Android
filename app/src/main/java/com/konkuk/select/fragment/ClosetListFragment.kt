package com.konkuk.select.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.activity.AddClosetActivity
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.adpater.ClosetListBlockAdapter
import com.konkuk.select.model.Closet
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.fragment_closet_list.*
import kotlinx.android.synthetic.main.fragment_closet_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class ClosetListFragment(val ctx: Context) : Fragment() {

    lateinit var closetListBlockAdapter:ClosetListBlockAdapter
    var closetList:ArrayList<Closet> = ArrayList()

    val TAG = "ClosetListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_closet_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingToolBar()
        settingAdapter()
        settingOnClickListener()
        getClosetData()
    }

    private fun settingToolBar(){
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            fragmentManager?.let {
                it.beginTransaction()
                    .replace(R.id.ll, ClosetFragment(ctx))
                    .commit()
            }
        }
    }

    private fun settingAdapter(){
        rv_closet_list.layoutManager = GridLayoutManager(ctx, 2)
        closetListBlockAdapter = ClosetListBlockAdapter(ctx, closetList)
        rv_closet_list.adapter = closetListBlockAdapter
        closetListBlockAdapter.itemClickListener = object :ClosetListBlockAdapter.OnItemClickListener{
            override fun onClickItem(
                holder: ClosetListBlockAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                fragmentManager?.let {
                    it.beginTransaction()
                        .replace(R.id.ll, ClosetFragment(ctx).apply {
                            arguments = Bundle().apply {
                                putString("closetId", data.id)
                                putString("closetTitle", data.name)
                            }
                        })
                        .commit()
                }
            }

            override fun onClickShareBtn(
                holder: ClosetListBlockAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                Toast.makeText(ctx, "옷장 공유 ${data.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun settingOnClickListener() {
        addClosetBtn.setOnClickListener {
            startActivity(Intent(ctx, AddClosetActivity::class.java))
        }
    }

    private fun getClosetData(){
        val closetRef  = Fbase.db.collection("closets").whereEqualTo("uid", Fbase.uid)
        closetRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null) {
                closetList.clear()
                for(doc in documentSnapshot.documents){
                    val closetObj = Fbase.getCloset(doc)
                    closetList.add(closetObj)
                }
                closetListBlockAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun sendShareLink(){

    }

}