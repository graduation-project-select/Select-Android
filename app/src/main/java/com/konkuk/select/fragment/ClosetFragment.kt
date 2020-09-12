package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.adpater.ClosetClothesHorizontalAdapter
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlin.random.Random


class ClosetFragment(val ctx: Context) : Fragment() {

    lateinit var closetCategoryListAdapter: ClosetCategoryListAdapter
    var categoryList: ArrayList<Category> =  arrayListOf<Category>(
            Category(0, "상의", true),
            Category(3, "아우터", false),
            Category(1, "하의", false),
            Category(4, "신발", false),
            Category(5, "악세서리", false),
            Category(2, "원피스", false)
        )
    var checkedCount: MutableLiveData<Int> = MutableLiveData(initCheckedCount())

    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    lateinit var closetClothesHorizontalAdapter: ClosetClothesHorizontalAdapter
    var clothesListVertical:ArrayList<Clothes> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // order로 번호로 정렬
        categoryList.sortBy({it.order})
        categoryList.forEach {
            Log.d("categoryList", it.order.toString() + ": " +it.label)
        }

        settingAdapter()
        settingOnClickListener()
    }

    fun settingAdapter(){
        rv_category.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        closetCategoryListAdapter = ClosetCategoryListAdapter(ctx, categoryList)
        rv_category.adapter = closetCategoryListAdapter

        //setting observer
        checkedCount.observe(this, Observer {
            Log.d("categoryList", "checkedCount: "+it)
            Log.d("categoryList", "categoryList: "+categoryList.toString())
            if(it == 1){
                // 한개면 한개인 항목의 데이터 fetch
                var selectedCategory:Category? = null
                for(cat in categoryList){
                    if(cat.checked) {
                        selectedCategory = cat
                        break;
                    }
                }
                Toast.makeText(ctx, selectedCategory!!.label + " 선택됨", Toast.LENGTH_SHORT).show()
                // 임시 데이터
                clothesListVertical.clear()
                for(i in 0..Random.nextInt(1, 10)){
                    clothesListVertical.add(Clothes(i.toString(),selectedCategory!!.label, ""))
                }
            }
            switchClothesListLayout(it)
        })

        closetCategoryListAdapter.itemClickListener = object:ClosetCategoryListAdapter.OnItemClickListener{
            override fun OnClickItem(
                holder: ClosetCategoryListAdapter.ItemHolder,
                view: View,
                data: Category,
                position: Int
            ) {
                if(categoryList[position].checked){
                    categoryList[position].checked = false
                    checkedCount.value = checkedCount.value?.minus(1)
                    if(checkedCount.value!! <= 0){
                        categoryList[position].checked = true
                        checkedCount.value = 1
                    }
                }else{
                    categoryList[position].checked = true
                    checkedCount.value = checkedCount.value?.plus(1)
                }
                holder.cb_category.isChecked = categoryList[position].checked
                closetCategoryListAdapter.notifyDataSetChanged()
                Log.d("categoryList", "Adapter_categoryList: $categoryList")
            }

        }

        rv_clothes_vertical.layoutManager = GridLayoutManager(ctx, 2)
        closetClothesVerticalAdapter = ClosetClothesVerticalAdapter(clothesListVertical)
        rv_clothes_vertical.adapter = closetClothesVerticalAdapter

        rv_clothes_horizontal.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        closetClothesHorizontalAdapter = ClosetClothesHorizontalAdapter(ctx, categoryList)
        rv_clothes_horizontal.adapter = closetClothesHorizontalAdapter

    }

    fun initCheckedCount():Int{
        var count = 0;
        for(c in categoryList){
            if(c.checked) count++
        }
        return count
    }

    fun switchClothesListLayout(checkedCount:Int){
        if(checkedCount == 1) {
            layout_clothes_vertical.visibility = View.VISIBLE
            layout_clothes_horizontal.visibility = View.GONE
            closetClothesVerticalAdapter.notifyDataSetChanged()
        }else if(checkedCount > 1){
            layout_clothes_vertical.visibility = View.GONE
            layout_clothes_horizontal.visibility = View.VISIBLE
            closetClothesHorizontalAdapter.notifyDataSetChanged()
        }else{
            layout_clothes_vertical.visibility = View.VISIBLE
            layout_clothes_horizontal.visibility = View.GONE
        }
    }

    fun settingOnClickListener(){
        toolbar.left_iv.setOnClickListener {
            Toast.makeText(ctx, "옷장 메뉴", Toast.LENGTH_SHORT).show()
            val t: FragmentTransaction = this.fragmentManager!!.beginTransaction()
            val mFrag: Fragment = ClosetListFragment(ctx)
            t.replace(R.id.ll, mFrag)
            t.commit()
//            val fragment: Fragment = ClosetListFragment(ctx)
//            val fragmentManager = activity!!.supportFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.ll, fragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
        }

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
