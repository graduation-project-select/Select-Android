package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.konkuk.select.R
import com.konkuk.select.activity.DetailClothesActivity
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.adpater.ClosetClothesHorizontalAdapter
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import com.konkuk.select.utils.StaticValues
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.json.JSONObject


class ClosetFragment(val ctx: Context) : Fragment() {

    // 카테고리
    lateinit var closetCategoryListAdapter: ClosetCategoryListAdapter
    var categoryList: ArrayList<Category> = StaticValues.categoryListTop
    lateinit var checkedCount: MutableLiveData<Int>

    var closetId: MutableLiveData<String> = MutableLiveData("")
    var closetTitle: MutableLiveData<String> = MutableLiveData("")

    // 세로 모드
    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    var clothesListVertical: ArrayList<Clothes> = arrayListOf()

    // 가로 모드
    lateinit var closetClothesHorizontalAdapter: ClosetClothesHorizontalAdapter

    val CLOSET_TAG = "closet"
    val FIREBASE_TAG = "firebase"

    val BOTTOMSHEET_CLOSETLIST_REQUEST_CODE = 1
    val CLOSET_ID_MESSAGE = "closetId"
    val CLOSET_TITLE_MESSAGE = "closetTitle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 옷장(ClosetListFragment)에서 넘어온 경우
        arguments?.getString(CLOSET_ID_MESSAGE)?.let {
            closetId.value = it
        }
        arguments?.getString(CLOSET_TITLE_MESSAGE)?.let {
            closetTitle.value = it
        }

    }

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
        settingObserver()
        settingOnClickListener()
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

    private fun settingToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.closet_btn)
        toolbar.left_iv.setOnClickListener {
            fragmentManager?.let {
                it.beginTransaction()
                    .replace(R.id.container, ClosetListFragment(ctx))
                    .commit()
            }
        }
        toolbar.right_tv.visibility = View.GONE
        toolbar.right_iv.setImageResource(R.drawable.alarm)
        toolbar.right_iv.setOnClickListener {
            Toast.makeText(ctx, "알림 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun settingObserver() {
        closetId.observe(this, Observer {
            // TODO 옷데이터 업데이트 -> livedata로 변경하여 observer에서 실행하게 바꾸기!!
            // TODO 옷리스트 업데이트 안됨..
            Log.d(CLOSET_TAG, "closetId observer")
//            init()
        })
        closetTitle.observe(this, Observer {
            tv_closet_name.text = if (closetTitle.value == "") "전체 옷장" else "${closetTitle.value}"
        })
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
                Log.d(CLOSET_TAG, selectedCategory!!.label + " 선택됨")
                getClothesByCategory(selectedCategory)
            }
            switchClothesListLayout(it)
        })
    }

    private fun settingAdapter() {
        rv_category.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        closetCategoryListAdapter = ClosetCategoryListAdapter(ctx, categoryList)
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
                    var intent = Intent(ctx, DetailClothesActivity::class.java)
                    intent.putExtra("clothesObj", data)
                    startActivity(intent)
                }
            }

        rv_clothes_horizontal.layoutManager = LinearLayoutManager(
            ctx,
            LinearLayoutManager.VERTICAL,
            false
        )
        closetClothesHorizontalAdapter =
            ClosetClothesHorizontalAdapter(ctx, categoryList, closetId.value!!)
        rv_clothes_horizontal.adapter = closetClothesHorizontalAdapter

    }

    // 카테고리에 해당하는 data fetch
    private fun getClothesByCategory(selectedCategory: Category) {
        var clothesRef = Fbase.db.collection("clothes")
            .whereEqualTo("category", selectedCategory.label)
            .whereEqualTo("uid", Fbase.uid)
        // 옷장이 선택된 경우
        if (closetId.value != "") clothesRef = clothesRef.whereArrayContains("closet", closetId)
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

    private fun settingOnClickListener() {
        tv_closet_name.setOnClickListener {
            // 아래에서 셀렉리스트 나오기
            Toast.makeText(ctx, "클릭!!", Toast.LENGTH_SHORT).show()
            showBottomSheetDialogFragment()
        }
    }

    private fun showBottomSheetDialogFragment() {
        val bottomSheetFragment = BottomSheetClosetListDialog(ctx)
        fragmentManager?.let {
            bottomSheetFragment.setTargetFragment(this, BOTTOMSHEET_CLOSETLIST_REQUEST_CODE)
            bottomSheetFragment.show(it, bottomSheetFragment.tag)
        }
    }

    fun passClosetData(id: String, title: String): Intent {
        val intent = Intent()
        intent.putExtra(CLOSET_ID_MESSAGE, id)
        intent.putExtra(CLOSET_TITLE_MESSAGE, title)
        return intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode !== Activity.RESULT_OK) {
            return
        }
        if (requestCode === BOTTOMSHEET_CLOSETLIST_REQUEST_CODE) {
            Log.d("closetTitle", BOTTOMSHEET_CLOSETLIST_REQUEST_CODE.toString())
            if (data != null) {
                data.getStringExtra(CLOSET_ID_MESSAGE)?.let {
                    closetId.value = it
                    Log.d("closetTitle", it)
                }
                data.getStringExtra(CLOSET_TITLE_MESSAGE)?.let {
                    closetTitle.value = it
                    Log.d("closetTitle", it)
                }
            }

        }
    }
}
