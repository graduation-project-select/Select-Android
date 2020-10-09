package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.konkuk.select.R
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.adpater.ClosetClothesHorizontalAdapter
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.closet_category_rv_item.view.*
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.json.JSONObject
import org.xml.sax.Parser

class ClosetFragment(val ctx: Context) : Fragment() {
    // 카테고리
    lateinit var closetCategoryListAdapter: ClosetCategoryListAdapter
    var categoryList: ArrayList<Category> = arrayListOf()
    lateinit var checkedCount: MutableLiveData<Int>
//    var categoryCheckedList = mutableMapOf<String, Boolean>()

    // 세로 모드
    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    var clothesListVertical: ArrayList<Clothes> = arrayListOf()
    // 가로 모드
    lateinit var closetClothesHorizontalAdapter: ClosetClothesHorizontalAdapter

    val CLOSET_TAG = "closet"
    val FIREBASE_TAG = "firebase"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_closet, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        settingToolBar()
        settingAdapter()
        settingOnClickListener()
    }

    private fun init(){
        // categoryList 초기화
        categoryList.addAll(
            arrayListOf<Category>(
                Category(0, "상의", true),
                Category(1, "하의", false),
                Category(2, "원피스", false),
                Category(3, "아우터", false),
                Category(4, "신발", false),
                Category(5, "악세서리", false)
            )
        )
        checkedCount =  MutableLiveData(initCheckedCount())
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

    private fun settingToolBar(){
        toolbar.left_iv.setImageResource(R.drawable.closet_btn)
        toolbar.left_iv.setOnClickListener {
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = ClosetListFragment(ctx)
            t.replace(R.id.container, mFrag)
            t.commit()
        }
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            Toast.makeText(ctx, "알림 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun settingAdapter() {
        rv_category.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        closetCategoryListAdapter = ClosetCategoryListAdapter(ctx, categoryList)
        rv_category.adapter = closetCategoryListAdapter

        //setting observer
        checkedCount.observe(this, Observer {
            Log.d(CLOSET_TAG, "checkedCount: " + it)
            Log.d(CLOSET_TAG, "categoryList: " + categoryList.toString())
            if (it == 1) {
                // 한개면 한개인 항목의 데이터 fetch
                var selectedCategory: Category? = null
                for (cat in categoryList) {
                    if (cat.checked) {
                        selectedCategory = cat
                        break;
                    }
                }
                Log.d(CLOSET_TAG, selectedCategory!!.label + " 선택됨")
                getClothesByCategory(selectedCategory)
            }
            switchClothesListLayout(it)
        })

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

        rv_clothes_vertical.layoutManager = GridLayoutManager(ctx, 2)
        closetClothesVerticalAdapter = ClosetClothesVerticalAdapter(ctx, clothesListVertical)
        rv_clothes_vertical.adapter = closetClothesVerticalAdapter
        closetClothesVerticalAdapter.itemClickListener =
            object : ClosetClothesVerticalAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: ClosetClothesVerticalAdapter.ImageHolder,
                    view: View,
                    data: Clothes,
                    position: Int
                ) {
                    // TODO 상세 페이지로 이동
                    Toast.makeText(ctx, "${data.id}, ${data.category} click", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        rv_clothes_horizontal.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        closetClothesHorizontalAdapter = ClosetClothesHorizontalAdapter(ctx, categoryList)
        rv_clothes_horizontal.adapter = closetClothesHorizontalAdapter

    }

    // 카테고리에 해당하는 data fetch
    private fun getClothesByCategory(selectedCategory: Category) {
        Fbase.db.collection("clothes")
            .whereEqualTo("category", selectedCategory.label)   //TODO whereEqualTo("uid", auth.uid)
            .get()
            .addOnSuccessListener { documents ->
                clothesListVertical.clear()
                for (document in documents) {
                    val jsonObj = JSONObject(document.data)
                    clothesListVertical.add(
                        Clothes(
                            document.id,
                            jsonObj["category"].toString(),
                            jsonObj["imgUrl"].toString()
                        )
                    )
                }
                closetClothesVerticalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(FIREBASE_TAG, "Error getting documents: ", exception)
            }
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

    fun settingOnClickListener() {
        tv_closet_name.setOnClickListener {
            // 아래에서 셀렉리스트 나오기
            Toast.makeText(ctx, "클릭!!", Toast.LENGTH_SHORT).show()
            showBottomSheetDialogFragment()
        }
    }

    fun showBottomSheetDialogFragment() {
        val bottomSheetFragment = BottomSheetFragmentDialog(ctx)
        fragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.getTag()) }
    }
}
