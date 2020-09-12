package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagListAdapter
import com.konkuk.select.model.CodiTag
import kotlinx.android.synthetic.main.fragment_codi_tag_list.*

class CodiTagListFragment(val ctx: Context) : Fragment() {
    lateinit var codiTagAdapter: CodiTagListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_codi_tag_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val leftBtn: ImageView = toolbar.findViewById(R.id.left_iv)
        val title: TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn: ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)

        leftBtn.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }

        var codiTagList = ArrayList<CodiTag>()
        codiTagList.add(CodiTag("111", "#데이트룩"))
        codiTagList.add(CodiTag("222", "#오피스룩"))
        codiTagList.add(CodiTag("333", "#캠퍼스룩"))

        codiTag_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        codiTagAdapter = CodiTagListAdapter(codiTagList)
        codiTag_rv.adapter = codiTagAdapter

        val simpleItemTouchCallback = object:ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                codiTagAdapter .onItemMove(p1.adapterPosition,p2.adapterPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
            }
        }

        var itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(codiTag_rv)

    }

}