package com.konkuk.select.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiBottomCategoryAdapter
import com.konkuk.select.adpater.CodiBottomClothesLinearAdapter
import com.konkuk.select.adpater.CodiBottomRecommendationAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import com.konkuk.select.utils.StaticValues
import kotlinx.android.synthetic.main.activity_add_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_codi_bottom.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Random
import kotlin.collections.ArrayList


class AddCodiActivity : AppCompatActivity() {
    var categoryList: ArrayList<String> = StaticValues.categoryList

    lateinit var codiBottomCategoryAdapter: CodiBottomCategoryAdapter   // 카테고리 목록
    lateinit var codiBottomClothesLinearAdapter: CodiBottomClothesLinearAdapter // 옷 목록
    lateinit var codiBottomRecommendationAdapter: CodiBottomRecommendationAdapter //추천 목록
    var codiBottomClothesList: ArrayList<Clothes> = arrayListOf()

    var codiClothesList: ArrayList<Clothes> = arrayListOf()  // 코디에 사용된 옷들

    val myClothes: ArrayList<Clothes> = arrayListOf() // 내 옷들
    lateinit var inputClothes: Clothes // 코디 추천에 input으로 준 옷
    var recommendItemsList: ArrayList<ArrayList<Clothes>> = arrayListOf() // 추천된 코디들
    var recommendItems: ArrayList<Clothes> = arrayListOf() // 코디 추천 옷들
    var codiClothesID: MutableSet<String> = mutableSetOf() // input으로 준 옷과 유사한 코디들 ID
    var hBool: MutableSet<String> = mutableSetOf()
    var sBool: MutableSet<String> = mutableSetOf()
    var vBool: MutableSet<String> = mutableSetOf()

    var isClothesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isHLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isVLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isAllLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isCodiRecmLodaing: MutableLiveData<Boolean> = MutableLiveData(false)

    var oldXvalue: Float = 0.0f
    var oldYvalue: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi)
        settingToolBar()
        settingAdapter()
        setClickListener()
        setObserver()
    }

    private fun settingToolBar() {
        toolbar.title_tv.text = getString(R.string.activity_title_addCodi)
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "완료"

        toolbar.left_iv.setOnClickListener {
            finish()
        }

        toolbar.right_tv.setOnClickListener {
            val imgByte = captureScreen(codi_canvas)
            var nextIntent = Intent(this, AddCodiRegisterActivity::class.java)
            nextIntent.putExtra("codiClothesList", codiClothesList)
            nextIntent.putExtra("codiImage", imgByte)
            startActivity(nextIntent)
            finish() // TODO 뒤로가기 해야되니깐 finish 하면 안되는데 일단..!
        }
    }

    private fun settingAdapter() {
        codiBottomCategoryAdapter = CodiBottomCategoryAdapter(categoryList)
        codiBottomClothesLinearAdapter = CodiBottomClothesLinearAdapter(this, codiBottomClothesList)
        codiBottomRecommendationAdapter = CodiBottomRecommendationAdapter(categoryList)
        bottom_rv.adapter = codiBottomCategoryAdapter
        switchLayoutManager()
    }

    private fun switchLayoutManager() {
        bottom_rv.layoutManager = when (bottom_rv.adapter) {
            codiBottomCategoryAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
            codiBottomClothesLinearAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
            codiBottomRecommendationAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
            else -> {
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun initClothesList(category: String) {
        Fbase.db.collection("clothes")
            .whereEqualTo("category", category)
            .whereEqualTo("uid", Fbase.auth.uid)
            .get()
            .addOnSuccessListener { documents ->
                codiBottomClothesList.clear()
                for (document in documents) {
                    val clothesObj = Fbase.getClothes(document)
                    codiBottomClothesList.add(clothesObj)
                }
                codiBottomClothesLinearAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents: ", exception)
            }
    }

    private fun setClickListener() {
        codiBottomCategoryAdapter.itemClickListener =
            object : CodiBottomCategoryAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: CodiBottomCategoryAdapter.ItemHolder,
                    view: View,
                    data: String,
                    position: Int
                ) {
                    toolbar_codi_bottom.tv_titile.text = data
                    initClothesList(data) // clothesList 변경 (temp data)
                    bottom_rv.adapter = codiBottomClothesLinearAdapter
                    switchLayoutManager()
                }
            }

        codiBottomClothesLinearAdapter.itemClickListener =
            object : CodiBottomClothesLinearAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: CodiBottomClothesLinearAdapter.ItemHolder,
                    view: View,
                    data: Clothes,
                    position: Int
                ) {
                    if (codiClothesList.contains(data)) {
                        Toast.makeText(this@AddCodiActivity, "중복 불가", Toast.LENGTH_SHORT).show()
                    } else {
                        codiClothesList.add(data)
                        var addImgView = ImageView(this@AddCodiActivity)
                        addImgView.layoutParams = ConstraintLayout.LayoutParams(450, 450)

                        Glide.with(this@AddCodiActivity)
                            .load(data.imgUri)
                            .into(addImgView)
                        codi_canvas.addView(addImgView)
                        dragAndDrop(addImgView)
                    }
                }

            }

        toolbar_codi_bottom.randomCodiBtn.setOnClickListener {
            toolbar_codi_bottom.tv_titile.text = "추천 아이템"
            Fbase.db.collection("clothes")
                .whereEqualTo("uid", Fbase.auth.uid)
                .get()
                .addOnSuccessListener { documents -> // 내 옷장에 있는 옷들
                    for (document in documents)
                        myClothes.add(Fbase.getClothes(document))
                    isClothesLoading.value = true
                }

            if (codiClothesList.isEmpty()) inputClothes = myClothes[Random().nextInt(myClothes.size)]
            else inputClothes = codiClothesList[0]

            getCodiFirebaseDocument(inputClothes)

            bottom_rv.adapter = codiBottomRecommendationAdapter
            switchLayoutManager()
        }

        toolbar_codi_bottom.categoryListBtn.setOnClickListener {
            toolbar_codi_bottom.tv_titile.text = "카테고리"
            bottom_rv.adapter = codiBottomCategoryAdapter
            switchLayoutManager()
        }
    }

    private fun setObserver() {
        isHLoading.observe(this, Observer {
            if (isHLoading.value!! && isSLoading.value!! && isVLoading.value!!) isAllLoading.value = true
        })
        isSLoading.observe(this, Observer {
            if (isHLoading.value!! && isSLoading.value!! && isVLoading.value!!) isAllLoading.value = true
        })
        isVLoading.observe(this, Observer {
            if (isHLoading.value!! && isSLoading.value!! && isVLoading.value!!) isAllLoading.value = true
        })
        isAllLoading.observe(this, Observer {
            if (isAllLoading.value!! && isClothesLoading.value!!) {
                for (h in hBool) if (sBool.contains(h) && vBool.contains(h)) codiClothesID.add(h)
                codiRecommend()
            }
        })
        isCodiRecmLodaing.observe(this, Observer {
            if (isCodiRecmLodaing.value!!) {
                Log.d("추천된 코디 개수: ", recommendItemsList.size.toString())
            }
        })
    }

    private fun codiRecommend() {
        /*
        * 0 : 상의
        * 1 : 하의
        * 2 : 드레스
        * 3 : 아우터
        * 4 : 신발
        * 5 : 액세서리
        * */

        Log.e("codiClothesID", codiClothesID.size.toString())
        for ((index, codiID) in codiClothesID.withIndex()) {
            recommendItems.clear()
            recommendItems.add(inputClothes)
            Log.d("현재 검색중인 codiID", codiID.toString())
            Fbase.db.collection("clothes_test")
                .whereEqualTo("codiId", codiID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val clothesObj = JSONObject(document.data)
                        var color = arrayListOf<Int>(clothesObj.getInt("color_h"), clothesObj.getInt("color_s"), clothesObj.getInt("color_v"))
                        val codiClothes = Clothes(
                            id = "",
                            category = clothesObj["category"] as String,
                            subCategory = clothesObj["subCategory"] as String,
                            texture = clothesObj["texture"] as String,
                            color = color,
                            season = arrayListOf<Boolean>(),
                            imgUri = "",
                            uid = ""
                        )

                        for (clothes in myClothes) {
                            if (codiClothes.category != inputClothes.category) {
                                if (compareClothes(clothes, codiClothes)) {
                                    recommendItems.add(clothes)
                                }
                            }
                        }
                    }
                    var onepiece = false
                    var twopiece: Array<Boolean> = arrayOf(false, false)
                    for (clothes in recommendItems) {
                        // 상의 하의 또는 드레스는 무조건 있어야함!!
                        if (clothes.category == "top") {
                            twopiece[0] = true
                        } else if (clothes.category == "bottom") {
                            twopiece[1] = true
                        } else if (clothes.category == "dress")
                            onepiece = true
                    }
                    if (onepiece || (twopiece[0] && twopiece[1])) {
                        Log.d("추천 코디의 codiID", codiID.toString())
                        Log.d("추천 코디 안의 옷 개수 : ", recommendItems.size.toString())
                        recommendItemsList.add(recommendItems)
                    }

                    if (index == codiClothesID.size - 1) {
                        isCodiRecmLodaing.value = true
                    }
                }
        }
    }

    private fun compareClothes(input1: Clothes, input2: Clothes): Boolean {
        if (input1.category == input2.category && input1.subCategory == input2.subCategory && input1.texture == input2.texture) {
            return (input1.color[0] >= (input2.color[0] - 10) && input1.color[0] <= (input2.color[0] + 10)) &&
                    (input1.color[1] >= (input2.color[1] - 10) && input1.color[1] <= (input2.color[1] + 10)) &&
                    (input1.color[2] >= (input2.color[2] - 10) && input1.color[2] <= (input2.color[2] + 10))
        } else return false
    }

    private fun getCodiFirebaseDocument(inputClothes: Clothes) {
        codiClothesID.clear()
        var allClothes: ArrayList<String> = arrayListOf()
        Fbase.db.collection("clothes_test").get().addOnSuccessListener { documents ->
            for (document in documents)
                allClothes.add(document["codiId"].toString())
        }

        Fbase.db.collection("clothes_test")
            .whereEqualTo("category", inputClothes.category)
            .whereEqualTo("subCategory", inputClothes.subCategory)
            .whereGreaterThanOrEqualTo("color_h", inputClothes.color[0] - 10)
            .whereLessThanOrEqualTo("color_h", inputClothes.color[0] + 10)
            .get()
            .addOnSuccessListener { documents ->
                hBool.clear()
                for (document in documents)
                    hBool.add(document["codiId"] as String)
                isHLoading.value = true
            }

        Fbase.db.collection("clothes_test")
            .whereEqualTo("category", inputClothes.category)
            .whereEqualTo("subCategory", inputClothes.subCategory)
            .whereGreaterThanOrEqualTo("color_s", inputClothes.color[1] - 10)
            .whereLessThanOrEqualTo("color_s", inputClothes.color[1] + 10)
            .get()
            .addOnSuccessListener { documents ->
                sBool.clear()
                for (document in documents)
                    sBool.add(document["codiId"] as String)
                isSLoading.value = true
            }

        Fbase.db.collection("clothes_test")
            .whereEqualTo("category", inputClothes.category)
            .whereEqualTo("subCategory", inputClothes.subCategory)
            .whereGreaterThanOrEqualTo("color_v", inputClothes.color[2] - 10)
            .whereLessThanOrEqualTo("color_v", inputClothes.color[2] + 10)
            .get()
            .addOnSuccessListener { documents ->
                vBool.clear()
                for (document in documents)
                    vBool.add(document["codiId"] as String)
                isVLoading.value = true
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragAndDrop(iv: ImageView) {
        iv.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val width = (v?.parent as ViewGroup).width - v.width
                val height = (v?.parent as ViewGroup).height - v.height
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    oldXvalue = event.x
                    oldYvalue = event.y
                    //  Log.i("Tag1", "Action Down X" + event.getX() + "," + event.getY());
                    Log.i("Tag1", "Action Down rX " + event.rawX + "," + event.rawY)
                } else if (event?.action == MotionEvent.ACTION_MOVE) {
                    v.x = event.rawX - oldXvalue
                    v.y = event.rawY - (oldYvalue + v.height / 2)
                    //  Log.i("Tag2", "Action Down " + me.getRawX() + "," + me.getRawY());
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    if (v.x > width && v.y > height) {
                        v.x = width.toFloat()
                        v.y = height.toFloat()
                    } else if (v.x < 0 && v.y > height) {
                        v.x = 0f
                        v.y = height.toFloat()
                    } else if (v.x > width && v.y < 0) {
                        v.x = width.toFloat()
                        v.y = 0f
                    } else if (v.x < 0 && v.y < 0) {
                        v.x = 0f
                        v.y = 0f
                    } else if (v.x < 0 || v.x > width) {
                        if (v.x < 0) {
                            v.x = 0f
                            v.y = event.rawY - oldYvalue - v.height
                        } else {
                            v.x = width.toFloat()
                            v.y = event.rawY - oldYvalue - v.height
                        }
                    } else if (v.y < 0 || v.y > height) {
                        if (v.y < 0) {
                            v.x = event.rawX - oldXvalue
                            v.y = 0f
                        } else {
                            v.x = event.rawX - oldXvalue
                            v.y = height.toFloat()
                        }
                    }
                }
                v.bringToFront()
                return true
            }
        })
    }

    private fun captureScreen(v: View): ByteArray {
        val bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val bgDrawable: Drawable = v.getBackground()
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        v.draw(canvas)
        val bytes = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        return bytes.toByteArray()
    }

}

/*
* json 데이터 밀어넣기 -> 이때 기존의 clothes랑 혼합될때 문제 없는지 확인 -> 색인 재설정
* 추천된 코디안에 옷들 가장 많은 것 순으로 추천 -> adapter 이미지 보여주기
* */