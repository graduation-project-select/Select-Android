package com.konkuk.select.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FieldValue
import com.konkuk.select.R
import com.konkuk.select.adpater.ClosetClothesVerticalAdapter
import com.konkuk.select.fragment.ClosetListFragment
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_add_closet.*
import kotlinx.android.synthetic.main.activity_add_closet.toolbar
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.json.JSONObject

class ClothesCheckListActivity : AppCompatActivity() {

    var closetId: String = "" // intent로 전달받은 값

    // 세로 모드
    lateinit var closetClothesVerticalAdapter: ClosetClothesVerticalAdapter
    var clothesListVertical: ArrayList<Clothes> = arrayListOf()
    var checkedClothesList = mutableSetOf<String>()

    val TAG = "ClothesCheckListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothes_check_list)

        if (intent.hasExtra("closetId")) {
            closetId = intent.getStringExtra("closetId").toString()
            Log.d("closetId", "받은거: $closetId")
        } else {
            Log.d(TAG, "closetId를 받지 못하였습니다")
            finish()
        }
        settingToolBar()
        getAllClothesList()
        settingAdapter()
    }

    private fun settingToolBar() {
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.title_tv.text = "옷 추가"
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "완료"
        toolbar.right_iv.visibility = View.GONE

        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_tv.setOnClickListener {
            // TODO 해당 옷장에 checkedClothesList 배열 데이터 추가
            Log.d(TAG, "완료")
            addClothesData()
            finish()
        }
    }

    private fun getAllClothesList() {
        // 파이어베이스에서 옷 리스트 전체 가져오기 (필터링 없음)
        Fbase.db.collection("clothes")
            .whereEqualTo("uid", Fbase.uid)
            .get()
            .addOnSuccessListener { documents ->
                clothesListVertical.clear()
                for (document in documents) {
                    val clothesObj = Fbase.getClothes(document)
                    clothesListVertical.add(clothesObj)
                }
                closetClothesVerticalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun settingAdapter() {
        rv_clothes_vertical.layoutManager = GridLayoutManager(this, 2)
        closetClothesVerticalAdapter = ClosetClothesVerticalAdapter(this, clothesListVertical)
        rv_clothes_vertical.adapter = closetClothesVerticalAdapter
        closetClothesVerticalAdapter.itemClickListener =
            object : ClosetClothesVerticalAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: ClosetClothesVerticalAdapter.ImageHolder,
                    view: View,
                    data: Clothes,
                    position: Int
                ) {
                    Toast.makeText(
                        this@ClothesCheckListActivity,
                        "${data.id} click",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (checkedClothesList.contains(data.id)) {
                        checkedClothesList.remove(data.id)
                        holder.checkMark.visibility = View.INVISIBLE
                    } else {
                        checkedClothesList.add(data.id)
                        holder.checkMark.visibility = View.VISIBLE
                    }
//                    Log.d(TAG, checkedClothesList.toString())
                }
            }
    }

    private fun addClothesData() {
        if (closetId != null) {
            val closetRef = Fbase.db.collection("closets").document(closetId)
            for (clothesId in checkedClothesList.toList()) {
                Log.d(TAG, clothesId)
                val clothesRef = Fbase.db.collection("clothes").document(clothesId)
                clothesRef.update("closet", FieldValue.arrayUnion(closetId))
                    .addOnSuccessListener {
                        Log.d(TAG, "$it")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            }
            closetRef.update("count", checkedClothesList.size)
        }
    }
}
