package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.select.R
import com.konkuk.select.adpater.BottomSheetClosetListAdapter
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.model.Closet
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.fragment_closet.*

class BottomSheetFragmentDialog(var ctx: Context) : BottomSheetDialogFragment() {

    var closetList:ArrayList<Closet> = ArrayList()
    lateinit var bottomSheetClosetListAdapter:BottomSheetClosetListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        for(i in 0..10)
            closetList.add(Closet(i.toString(), "운동 갈 때", "12", ""))

        rv_closet_list.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        bottomSheetClosetListAdapter = BottomSheetClosetListAdapter(closetList)
        rv_closet_list.adapter = bottomSheetClosetListAdapter

        bottomSheetClosetListAdapter.itemClickListener = object : BottomSheetClosetListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: BottomSheetClosetListAdapter.ItemHolder,
                view: View,
                data: Closet,
                position: Int
            ) {
                var str = data.id + " " + data.title + " " + data.count
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show()
            }

        }
    }

}