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
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagListAdapter
import com.konkuk.select.model.CodiTag
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_codi_tag_list.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiTagListFragment(val ctx: Context) : Fragment() {

    lateinit var codiTagAdapter: CodiTagListAdapter
    var myTagList: ArrayList<CodiTag> = arrayListOf()

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
        getUserCodiTagList()
        setClickListener()
    }

    private fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = CodiFragment(ctx)
            t.replace(R.id.codill, mFrag)
            t.commit()
        }
    }

    private fun setAdapter() {
        codiTag_rv.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        codiTagAdapter = CodiTagListAdapter(myTagList)
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
                val bundle = Bundle()
                bundle.putString("tagId", data.ref.id)
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

    private fun getUserCodiTagList() {
        Fbase.uid?.let {
            Fbase.USERS_REF.document(it).get().addOnSuccessListener {
                val codiTagList = it.get("codiTagList") as ArrayList<DocumentReference>
                myTagList.clear()
                for (tagRef in codiTagList) {
                    tagRef.get().addOnSuccessListener {tagObj ->
                        myTagList.add(CodiTag(tagRef, tagObj["name"].toString()))
                        codiTagAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}