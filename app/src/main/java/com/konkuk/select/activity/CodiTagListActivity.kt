package com.konkuk.select.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagListAdapter
import com.konkuk.select.model.CodiTag
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_codi_tag_list.*
import kotlinx.android.synthetic.main.toolbar.view.*

private const val TAG_ID_MESSAGE = "tagId"
private const val TAG_NAME_MESSAGE = "tagName"

class CodiTagListActivity : AppCompatActivity() {

    lateinit var codiTagAdapter: CodiTagListAdapter
    var myTagList: ArrayList<CodiTag> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codi_tag_list)
        setToolBar()
        setAdapter()
        getUserCodiTagList()
        setClickListener()
    }

    private fun setToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_iv.visibility = View.INVISIBLE
    }

    private fun setAdapter() {
        codiTag_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
                Toast.makeText(this@CodiTagListActivity, "${data.ref}, ${data.tag} click", Toast.LENGTH_SHORT).show()
                var nextIntent = Intent(this@CodiTagListActivity, CodiListDetailActivity::class.java)
                nextIntent.putExtra(TAG_ID_MESSAGE, data.ref.id)
                nextIntent.putExtra(TAG_NAME_MESSAGE, data.tag)
                startActivity(nextIntent)
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
                if(it.get("codiTagList") != null) {
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

}