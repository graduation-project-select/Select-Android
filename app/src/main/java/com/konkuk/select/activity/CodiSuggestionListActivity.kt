package com.konkuk.select.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiListAdapter
import com.konkuk.select.adpater.CodiSuggestionListAdapter
import com.konkuk.select.model.CodiSugNoti
import com.konkuk.select.model.CodiSuggestion
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_codi_suggestion_list.*
import kotlinx.android.synthetic.main.activity_codi_tag_list.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*
import okhttp3.internal.notify

class CodiSuggestionListActivity : AppCompatActivity() {

    lateinit var codiSuggestionListAdapter: CodiSuggestionListAdapter
    var codiList: ArrayList<CodiSuggestion> = arrayListOf()

    var isSharing: Boolean = false
    var closetId: String = ""
    var ownerUid: String = ""
    var senderUid: String = ""


    var codiSugNotiId: String = ""
    lateinit var codiSugNotiObj: CodiSugNoti

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
        // 추천해주면서 확인용
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
            getUserName(senderUid)
        }
        // 알림에서 확인하러 들어왔을 때
        if (intent.hasExtra("codiSugNotiId")) {
            intent.getStringExtra("codiSugNotiId")?.let{
                codiSugNotiId = it
            }
            getCodiSugNotiObj(codiSugNotiId)
        }
    }

    private fun getCodiData(closetId: String, ownerUid: String, senderUid: String) {
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

    fun getCodiSugNotiObj(codiSugNotiId: String) {
        Fbase.CODISUG_NOTI_REF.document(codiSugNotiId)
            .get().addOnSuccessListener {
                if(it.exists()){
                    codiSugNotiObj = Fbase.getCodiSugNoti(it)
                    getUserName(codiSugNotiObj.senderUid)
                    getSuggestedCodiData(codiSugNotiObj.codiIds)
                }
            }
    }

    fun getUserName(userId: String) {
        Fbase.USERS_REF.document(userId).get().addOnSuccessListener {
            var name: String = it.get("name") as String
            tv_senderName.text = name
        }
    }


    fun getSuggestedCodiData(idList:ArrayList<String>){
        codiList.clear()
        for(codiSugId in idList){
            Fbase.CODI_SUGGESTION_REF.document(codiSugId)
                .get().addOnSuccessListener {
                    val codiSugObj=  Fbase.getCodiSuggestion(it)
                    codiList.add(codiSugObj)
                    codiSuggestionListAdapter.notifyDataSetChanged()
                }
        }
    }



}
