package com.konkuk.select.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.select.R
import com.konkuk.select.activity.AddCodiActivity
import com.konkuk.select.adpater.BottomSheetClosetListAdapter
import com.konkuk.select.model.Closet
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet_mainbtn_dialog.*

class BottomSheetMainBtnDialog(var ctx: Context) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_mainbtn_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_addClothesBtn.setOnClickListener {
            Toast.makeText(ctx, "옷 등록", Toast.LENGTH_SHORT).show()
        }

        tv_addCodiBtn.setOnClickListener {
            Toast.makeText(ctx, "코디 등록", Toast.LENGTH_SHORT).show()
            val nextIntent = Intent(ctx, AddCodiActivity::class.java)
            startActivity(nextIntent)
        }
    }

}