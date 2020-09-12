package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.adpater.ClosetListBlockAdapter
import com.konkuk.select.model.Closet
import kotlinx.android.synthetic.main.fragment_closet_list.*

class ClosetListFragment(val ctx: Context) : Fragment() {
    lateinit var closetListBlockAdapter:ClosetListBlockAdapter
    var closetList:ArrayList<Closet> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_closet_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val leftBtn : ImageView = toolbar.findViewById(R.id.left_iv)
        val title : TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = toolbar.findViewById(R.id.right_iv)

        for(i in 0..10)
            closetList.add(Closet(i.toString(), "운동 갈 때", "12", ""))

        rv_closet_list.layoutManager = GridLayoutManager(ctx, 2)
        closetListBlockAdapter = ClosetListBlockAdapter(ctx, closetList)
        rv_closet_list.adapter = closetListBlockAdapter

        leftBtn.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = ClosetFragment(ctx)
            t.replace(R.id.ll, mFrag)
            t.commit()
        }
    }

}