package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.model.Codi
import kotlinx.android.synthetic.main.fragment_codi_list_detail_flagment.*
import kotlinx.android.synthetic.main.fragment_codi_tag_list.*
import kotlinx.android.synthetic.main.fragment_codi_tag_list.toolbar

class CodiListDetailFlagment(val ctx: Context) : Fragment() {
    lateinit var codiListAdapter: CodiListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_codi_list_detail_flagment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val leftBtn: ImageView = toolbar.findViewById(R.id.left_iv)
        val title: TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn: ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)

        leftBtn.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiTagListFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }

        var codiList = ArrayList<Codi>()
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))
        codiList.add(Codi("111", "#데이트룩", "0"))


        codiList_rv.layoutManager = GridLayoutManager(ctx, 2)
        codiListAdapter = CodiListAdapter(codiList)
        codiList_rv.adapter = codiListAdapter
    }
}