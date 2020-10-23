package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.adpater.CodiSuggestionListAdapter
import com.konkuk.select.model.CodiSuggestion
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_codi_suggestion_list.*
import kotlinx.android.synthetic.main.activity_codi_tag_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*

class CodiSuggestionListActivity : AppCompatActivity() {

    lateinit var codiSuggestionListAdapter: CodiSuggestionListAdapter
    var codiList:ArrayList<CodiSuggestion> = arrayListOf()

    var isSharing: Boolean = false
    var closetId: String = ""
    var ownerUid: String = ""
    var senderUid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codi_suggestion_list)
        setToolbar()
        setAdapter()
        getDataFromIntent()
    }

    fun setToolbar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_iv.visibility = View.INVISIBLE
    }

    fun setAdapter() {
        rv_codiSuggestion.layoutManager = GridLayoutManager(this, 2)
        codiSuggestionListAdapter = CodiSuggestionListAdapter(codiList)
        rv_codiSuggestion.adapter = codiSuggestionListAdapter
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra("isSharing")) {
            isSharing = intent.getBooleanExtra("isSharing", false)
            intent.getStringExtra("closetId")?.let {
                closetId = it
            }
            intent.getStringExtra("ownerUid")?.let {
                ownerUid = it
            }
            intent.getStringExtra("senderUid")?.let {
                senderUid = it
            }
            getCodiData(closetId, ownerUid, senderUid)
        }
    }

    private fun getCodiData(closetId:String, ownerUid:String, senderUid:String){
        Fbase.CODI_SUGGESTION_REF
            .whereEqualTo("closetId", closetId)
            .whereEqualTo("ownerUid", ownerUid)
            .whereEqualTo("senderUid", senderUid)
            .get().addOnSuccessListener { documents ->
                codiList.clear()
                for (document in documents) {
                    val codiObj = Fbase.getCodiSuggestion(document)
                    codiList.add(codiObj)
                }
                codiSuggestionListAdapter.notifyDataSetChanged()
            }
    }
}
