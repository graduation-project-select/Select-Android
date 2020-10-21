package com.konkuk.select.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.select.R
import com.konkuk.select.adpater.BottomSheetClosetListAdapter
import com.konkuk.select.model.Closet
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.fragment_bottom_sheet_closetlist_dialog.*


class BottomSheetClosetListDialog : BottomSheetDialogFragment() {

    var closetList:ArrayList<Closet> = ArrayList()
    lateinit var bottomSheetClosetListAdapter:BottomSheetClosetListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_closetlist_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getClosetData()
        rv_closet_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        bottomSheetClosetListAdapter = BottomSheetClosetListAdapter(closetList)
        rv_closet_list.adapter = bottomSheetClosetListAdapter

        bottomSheetClosetListAdapter.itemClickListener = object : BottomSheetClosetListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: BottomSheetClosetListAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                Log.d("BottomSheetClosetListDialog",data.id + " " + data.name + " " + data.count )
                if (targetFragment != null) {
                    val intent: Intent = ClosetFragment().passClosetData(data.id, data.name)
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }else{
                    Log.w("BottomSheetClosetListDialog", "targetFragment null")
                    dismiss()
                }
            }

        }
    }

    private fun getClosetData(){
        val closetRef  = Fbase.db.collection("closets").whereEqualTo("uid", Fbase.uid)
        closetRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null) {
                closetList.clear()
                for(doc in documentSnapshot.documents){
                    val closetObj = Fbase.getCloset(doc)
                    closetList.add(closetObj)
                }
                bottomSheetClosetListAdapter.notifyDataSetChanged()
            }
        }
    }

}