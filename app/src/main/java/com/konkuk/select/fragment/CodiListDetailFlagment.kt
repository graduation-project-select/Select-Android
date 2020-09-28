package com.konkuk.select.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.konkuk.select.activity.DetailCodiActivity
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.model.Codi
import kotlinx.android.synthetic.main.fragment_codi.*
import kotlinx.android.synthetic.main.fragment_codi_list_detail_flagment.*
import kotlinx.android.synthetic.main.fragment_codi_tag_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

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

        val bundle: Bundle? = arguments

        if(bundle != null) {
            tv_codi_tag.text = bundle.getString("tag")
        }

        setToolBar()
        setAdapter()
        setClickListener()
    }

    fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiTagListFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

    fun setAdapter() {
        var codiList = ArrayList<Codi>()
        codiList.add(Codi("111", "#데이트룩", "0", true))
        codiList.add(Codi("222", "#데이트룩", "0", true))
        codiList.add(Codi("333", "#데이트룩", "0", true))
        codiList.add(Codi("444", "#데이트룩", "0", true))
        codiList.add(Codi("555", "#데이트룩", "0", true))
        codiList.add(Codi("666", "#데이트룩", "0", true))

        codiList_rv.layoutManager = GridLayoutManager(ctx, 2)
        codiListAdapter = CodiListAdapter(codiList)
        codiList_rv.adapter = codiListAdapter
    }

    fun setClickListener() {
        codiListAdapter.itemClickListener = object : CodiListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: CodiListAdapter.ItemHolder,
                view: View,
                data: Codi,
                position: Int
            ) {
                Toast.makeText(ctx, "${data.id}, ${data.tag} click", Toast.LENGTH_SHORT).show()
                val intent = Intent(ctx, DetailCodiActivity::class.java)
                startActivity(intent)
            }
        }
    }
}