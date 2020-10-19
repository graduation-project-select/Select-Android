package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.android.synthetic.main.toolbar.view.*

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

        setToolBar()
        setAdapter()
        setClickListener()
    }

    fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

    fun setAdapter() {
        var codiTagList = ArrayList<CodiTag>()
//        codiTagList.add(CodiTag("111", "#데이트룩"))
//        codiTagList.add(CodiTag("222", "#오피스룩"))
//        codiTagList.add(CodiTag("333", "#캠퍼스룩"))

        codiTag_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        codiTagAdapter = CodiTagListAdapter(codiTagList)
        codiTag_rv.adapter = codiTagAdapter
    }

    fun setClickListener() {
        codiTagAdapter.itemClickListener = object:CodiTagListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: CodiTagListAdapter.ItemHolder,
                view: View,
                data: CodiTag,
                position: Int
            ) {
                Toast.makeText(ctx, "${data.ref}, ${data.tag} click", Toast.LENGTH_SHORT).show()
                val t: FragmentTransaction = fragmentManager!!.beginTransaction()
                val mFrag: Fragment = CodiListDetailFragment(ctx)
                val bundle:Bundle = Bundle()
                bundle.putString("tag", data.tag)
                mFrag.setArguments(bundle)
                t.replace(R.id.codill, mFrag)
                t.commit()
            }
        }

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