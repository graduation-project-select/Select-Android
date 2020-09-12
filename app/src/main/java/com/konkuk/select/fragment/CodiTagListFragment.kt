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
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagListAdapter
import com.konkuk.select.model.CodiTag
import kotlinx.android.synthetic.main.activity_detail_cloth.*
import kotlinx.android.synthetic.main.fragment_codi_tag_list.toolbar

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

        val leftBtn : ImageView = toolbar.findViewById(R.id.left_iv)
        val title : TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)

        var codiTagList = ArrayList<CodiTag>()
        codiTagList.add(CodiTag("111", "#데이트룩"))
        codiTagList.add(CodiTag("222", "#오피스룩"))
        codiTagList.add(CodiTag("333", "#캠퍼스룩"))

        codi_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        codiTagAdapter = CodiTagListAdapter(codiTagList)
        codi_rv.adapter = codiTagAdapter

        leftBtn.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

}