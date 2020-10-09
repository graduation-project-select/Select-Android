package com.konkuk.select.activity

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiBottomCategoryAdapter
import com.konkuk.select.adpater.CodiBottomClothesLinearAdapter
import com.konkuk.select.adpater.CodiBottomRecommendationAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import kotlinx.android.synthetic.main.activity_add_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_codi_bottom.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.random.Random


class AddCodiActivity : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    var categoryList: ArrayList<Category> = arrayListOf<Category>(
        Category(0, "상의", true),
        Category(1, "하의", false),
        Category(2, "원피스", false),
        Category(3, "아우터", false),
        Category(4, "신발", false),
        Category(5, "악세서리", false)
    )

    lateinit var codiBottomCategoryAdapter: CodiBottomCategoryAdapter
    lateinit var codiBottomRecommendationAdapter: CodiBottomRecommendationAdapter
    lateinit var codiBottomClothesLinearAdapter: CodiBottomClothesLinearAdapter
    var clothesList: ArrayList<Clothes> = arrayListOf()

    var oldXvalue: Float = 0.0f
    var oldYvalue: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi)
        setToolBar()
        setAdapter()
        setClickListener()

    }

    fun setToolBar() {
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
            nextIntent.putExtra("codiImage", imgByte)
            startActivity(nextIntent)
            finish() // 뒤로가기 해야되니깐 finish 하면 안되는데 일단..!
        }
    }

    fun setAdapter() {
        codiBottomCategoryAdapter = CodiBottomCategoryAdapter(categoryList)
        codiBottomRecommendationAdapter =
            CodiBottomRecommendationAdapter(arrayListOf("상의", "하의", "원피스", "아우터", "신발", "악세서리"))
        codiBottomClothesLinearAdapter = CodiBottomClothesLinearAdapter(this, clothesList)
        bottom_rv.adapter = codiBottomCategoryAdapter
        switchLayoutManager()
    }

    //temp
    fun initTempData(category: String) {
//        clothesList.clear()
//        for (i in 0..Random.nextInt(1, 5)) {
//            clothesList.add(Clothes(i.toString(), category, "cloth_test"))
//        }
        val docRef = db.collection("clothes")
            .whereEqualTo("category", category)   //TODO whereEqualTo("uid", auth.uid)
            .get()
            .addOnSuccessListener { documents ->
                clothesList.clear()
                for (document in documents) {
                    val jsonObj = JSONObject(document.data)
                    clothesList.add(Clothes(document.id,category, jsonObj["imgUrl"].toString()))
//                            clothesListVertical.add(Clothes(document.id, jsonObj["category"].toString(), jsonObj["subCategory"].toString(), jsonObj["checkedArray"], jsonObj["imgUrl"].toString()))
//                            val id: String, val category: String, val subCategory: String, val checkedArr:ArrayList<Boolean>, val R:Int, val G:Int, val B:Int, val imgUrl:String)
//                            Log.d("firebase", "${document.id} => ${document.data}")
//                            Log.d("firebase", "${jsonObj["imgUrl"]}")
                }
                codiBottomClothesLinearAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents: ", exception)
            }
    }

    fun setClickListener() {
        codiBottomCategoryAdapter.itemClickListener =
            object : CodiBottomCategoryAdapter.OnItemClickListener {
                override fun OnClickItem(
                    holder: CodiBottomCategoryAdapter.ItemHolder,
                    view: View,
                    data: Category,
                    position: Int
                ) {
                    toolbar_codi_bottom.tv_titile.text = data.label
                    initTempData(data.label) // clothesList 변경 (temp data)
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
                    Toast.makeText(
                        this@AddCodiActivity,
                        "${data.id} ${data.category} click",
                        Toast.LENGTH_SHORT
                    ).show()
                    var addImgView = ImageView(this@AddCodiActivity)
                    addImgView.layoutParams = ConstraintLayout.LayoutParams(450, 450)
//                    addImgView.setImageResource(R.drawable.cloth_test)
                    Glide.with(this@AddCodiActivity)
                        .load(data.img)
                        .into(addImgView)
                    codi_canvas.addView(addImgView)
                    draganddrop(addImgView)
                }

            }

        toolbar_codi_bottom.randomCodiBtn.setOnClickListener {
            toolbar_codi_bottom.tv_titile.text = "추천 아이템"
            bottom_rv.adapter = codiBottomRecommendationAdapter
            switchLayoutManager()
        }

        toolbar_codi_bottom.categoryListBtn.setOnClickListener {
            toolbar_codi_bottom.tv_titile.text = "카테고리"
            bottom_rv.adapter = codiBottomCategoryAdapter
            switchLayoutManager()
        }

    }

    fun switchLayoutManager() {
        bottom_rv.layoutManager = when (bottom_rv.adapter) {
            codiBottomCategoryAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
            codiBottomRecommendationAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
            codiBottomClothesLinearAdapter -> {
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
            else -> {
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    fun draganddrop(iv: ImageView) {
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

    fun captureScreen(v: View) : ByteArray {
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