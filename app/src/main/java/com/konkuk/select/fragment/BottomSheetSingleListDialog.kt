package com.konkuk.select.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.select.R
import com.konkuk.select.adpater.CategoryPickerAdapter
import com.konkuk.select.adpater.CodiBottomCategoryAdapter
import com.konkuk.select.utils.StaticValues
import kotlinx.android.synthetic.main.fragment_bottom_sheet_closetlist_dialog.*

class BottomSheetSingleListDialog() : BottomSheetDialogFragment() {

    lateinit var codiBottomCategoryAdapter: CategoryPickerAdapter   // 카테고리 목록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_closetlist_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_title.text = "카테고리 선택"

        rv_closet_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        codiBottomCategoryAdapter = CategoryPickerAdapter(StaticValues.categoryList)
        rv_closet_list.adapter = codiBottomCategoryAdapter

        codiBottomCategoryAdapter.itemClickListener =
            object : CategoryPickerAdapter.OnItemClickListener {

                override fun OnClickItem(category: String, subCategory: String) {
                    (activity as onChangeCategory).getSelectedCategory(category, subCategory)
                    dismiss()
                }
            }

    }

    interface onChangeCategory {
        fun getSelectedCategory(category:String, subCategory:String)
    }

}