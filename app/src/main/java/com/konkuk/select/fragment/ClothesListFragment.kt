package com.konkuk.select.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.konkuk.select.R
import com.konkuk.select.activity.DetailClothesActivity
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.adpater.ClosetClothesHorizontalAdapter
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import com.konkuk.select.utils.StaticValues
import kotlinx.android.synthetic.main.fragment_clothes_list.*

// Params
private const val CLOSET_ID_PARAM = "closetId"
private const val USER_ID_PARAM = "userId"

// Log tags
private const val CLOSET_TAG = "closet"
private const val FIREBASE_TAG = "firebase"

class ClothesListFragment : Fragment() {
    private var closetId: String = ""
    private var uid: String = ""

    companion object {
        @JvmStatic
        fun newInstance(closetId: String, uid: String) =
            ClothesListFragment().apply {
                arguments = Bundle().apply {
                    putString(CLOSET_ID_PARAM, closetId)
                    putString(USER_ID_PARAM, uid)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            closetId = it.getString(CLOSET_ID_PARAM).toString()
            uid = it.getString(USER_ID_PARAM).toString()
            Log.d("서영3", "closetId: $closetId, uid: $uid")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // 카테고리
    lateinit var checkedCount: MutableLiveData<Int>
    lateinit var closetCategoryListAdapter: ClosetCategoryListAdapter
    var categoryList: ArrayList<Category> = StaticValues.categoryListTop

    // 세로 모드
    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    var clothesListVertical: ArrayList<Clothes> = arrayListOf()

    // 가로 모드
    lateinit var closetClothesHorizontalAdapter: ClosetClothesHorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clothes_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Toast.makeText(activity, closetId + ", " + uid, Toast.LENGTH_SHORT).show()
        init()
        settingObserver()
        settingAdapter()
    }

    private fun init() {
        checkedCount = MutableLiveData(initCheckedCount())
//        // order로 번호로 정렬
//        categoryList.sortBy({ it.order })
//        categoryList.forEach {
//            Log.d("categoryList", it.order.toString() + ": " + it.label)
//        }
    }

    private fun initCheckedCount(): Int {
        var count = 0;
        for (c in categoryList) {
            if (c.checked) count++
        }
        return count
    }

    private fun settingObserver() {
        checkedCount.observe(this, Observer {
            Log.d(CLOSET_TAG, "categoryList: $categoryList (checkedCount: $it)")
            if (it == 1) {
                // 한개면 한개인 항목의 데이터 fetch
                var selectedCategory: Category? = null
                for (cat in categoryList) {
                    if (cat.checked) {
                        selectedCategory = cat
                        break;
                    }
                }
                getClothesByCategory(selectedCategory!!.label, closetId!!, uid!!)
            }
            switchClothesListLayout(it)
        })
    }

    private fun switchClothesListLayout(checkedCount: Int) {
        if (checkedCount <= 1) {
            layout_clothes_vertical.visibility = View.VISIBLE
            layout_clothes_horizontal.visibility = View.GONE
            closetClothesVerticalAdapter.notifyDataSetChanged()
        } else if (checkedCount > 1) {
            layout_clothes_vertical.visibility = View.GONE
            layout_clothes_horizontal.visibility = View.VISIBLE
            closetClothesHorizontalAdapter.notifyDataSetChanged()
        }
    }

    private fun settingAdapter() {
        rv_category.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        closetCategoryListAdapter = ClosetCategoryListAdapter(categoryList)
        rv_category.adapter = closetCategoryListAdapter

        closetCategoryListAdapter.itemClickListener =
            object : ClosetCategoryListAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: ClosetCategoryListAdapter.ItemHolder,
                    view: View,
                    data: Category,
                    position: Int
                ) {
                    categoryList[position].checked = (view as CheckBox).isChecked
                    if (categoryList[position].checked) {
                        checkedCount.value = checkedCount.value?.plus(1)
                    } else {
                        checkedCount.value = checkedCount.value?.minus(1)
                        if (checkedCount.value!! <= 0) {
                            categoryList[position].checked = true
                            holder.cb_category.isChecked = categoryList[position].checked
                            checkedCount.value = 1
                        }
                    }
                    closetCategoryListAdapter.notifyDataSetChanged()
                    Log.d(CLOSET_TAG, "Adapter_categoryList: $categoryList")
                    Log.d(CLOSET_TAG, "checkedCount.value: ${checkedCount.value}")
                }

            }

        rv_clothes_vertical.layoutManager = GridLayoutManager(activity, 2)
        closetClothesVerticalAdapter = ClosetClothesVerticalAdapter(clothesListVertical)
        rv_clothes_vertical.adapter = closetClothesVerticalAdapter
        closetClothesVerticalAdapter.itemClickListener =
            object : ClosetClothesVerticalAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: ClosetClothesVerticalAdapter.ImageHolder,
                    view: View,
                    data: Clothes,
                    position: Int
                ) {
                    var intent = Intent(activity, DetailClothesActivity::class.java)
                    intent.putExtra("clothesObj", data)
                    startActivity(intent)
                }
            }

        rv_clothes_horizontal.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        closetClothesHorizontalAdapter =
            ClosetClothesHorizontalAdapter(categoryList, closetId!!, uid!!)
        rv_clothes_horizontal.adapter = closetClothesHorizontalAdapter

    }

    // 카테고리에 해당하는 data fetch
    private fun getClothesByCategory(category: String, closetId: String, userId: String) {
        var clothesRef = Fbase.CLOTHES_REF
            .whereEqualTo("category", category)
            .whereEqualTo("uid", userId)
        // 옷장이 선택된 경우
        if (closetId != "") clothesRef = clothesRef.whereArrayContains("closet", closetId)
        clothesRef.get()
            .addOnSuccessListener { documents ->
                clothesListVertical.clear()
                for (document in documents) {
                    val clothesObj = Fbase.getClothes(document)
                    clothesListVertical.add(clothesObj)
                }
                closetClothesVerticalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(FIREBASE_TAG, "Error getting documents: ", exception)
            }
    }

}
