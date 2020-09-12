package com.konkuk.select.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.select.R
import com.konkuk.select.adpater.ClosetCategoryListAdapter
import com.konkuk.select.adpater.ClosetClothesHorizontalAdapter
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.model.Clothes
import kotlinx.android.synthetic.main.fragment_closet.*


class ClosetFragment(val ctx: Context) : Fragment() {

    lateinit var closetCategoryListAdapter: ClosetCategoryListAdapter
    var categoryList: ArrayList<String> = arrayListOf<String>(
        "상의",
        "하의",
        "원피스",
        "아우터",
        "신발",
        "악세서리"
    )
    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    var temp_imgs:ArrayList<String> = ArrayList()
    var temp_temp_imgs:ArrayList<ArrayList<Clothes>> = ArrayList()

    lateinit var closetClothesHorizontalAdapter: ClosetClothesHorizontalAdapter

//    var clicked_category:ArrayList<String> = ArrayList()

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

        // ToolBar 변경하는 코드
//        val includeToolBar : View = findViewById(R.id.toolbar)
        val leftBtn : ImageView = toolbar.findViewById(R.id.left_iv)
        val title : TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = toolbar.findViewById(R.id.right_iv)
//        for(i in 0..10)
//            temp_imgs.add("")

        // 리사이클러뷰를 보여주기 위한 가짜 데이터
        for(i in 0..3) {
            var t_arr: ArrayList<Clothes> = ArrayList()
            for (j in 0..10)
                t_arr.add(Clothes(j.toString()))
            temp_temp_imgs.add(t_arr)
        }

        // setOnClickListener
        leftBtn.setOnClickListener {
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

        rv_category.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        closetCategoryListAdapter = ClosetCategoryListAdapter(ctx, categoryList)
        rv_category.adapter = closetCategoryListAdapter

        closetCategoryListAdapter.itemClickListener  = object : ClosetCategoryListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: ClosetCategoryListAdapter.ItemHolder,
                view: View,
                data: String,
                position: Int
            ) {
//                var dataIndex = clicked_category.indexOf(data)
//                if(dataIndex > 0)
//                    clicked_category.removeAt(dataIndex)
//                else
//                    clicked_category.add(data)
            }
        }

        rv_clothes_vertical.layoutManager = GridLayoutManager(ctx, 2)
        closetClothesVerticalAdapter = ClosetClothesVerticalAdapter(temp_imgs)
        rv_clothes_vertical.adapter = closetClothesVerticalAdapter

        rv_clothes_horizontal.layoutManager = LinearLayoutManager(
            ctx,
            LinearLayoutManager.VERTICAL,
            false
        )
        closetClothesHorizontalAdapter = ClosetClothesHorizontalAdapter(ctx, temp_temp_imgs)
        rv_clothes_horizontal.adapter = closetClothesHorizontalAdapter

        // 옷장 선택 버튼
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
