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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiBottomCategoryAdapter
import com.konkuk.select.adpater.CodiBottomClothesLinearAdapter
import com.konkuk.select.adpater.CodiBottomRecommendationAdapter
import com.konkuk.select.model.Category
import com.konkuk.select.model.Clothes
import com.konkuk.select.network.Fbase
import com.konkuk.select.utils.StaticValues
import kotlinx.android.synthetic.main.activity_add_codi.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_codi_bottom.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class AddCodiActivity : AppCompatActivity() {
    var categoryList: ArrayList<String> = StaticValues.categoryList

    lateinit var codiBottomCategoryAdapter: CodiBottomCategoryAdapter   // 카테고리 목록
    lateinit var codiBottomClothesLinearAdapter: CodiBottomClothesLinearAdapter // 옷 목록
    lateinit var codiBottomRecommendationAdapter: CodiBottomRecommendationAdapter //추천 목록
    var codiBottomClothesList: ArrayList<Clothes> = arrayListOf()

    var codiClothesList:ArrayList<Clothes> = arrayListOf()  // 코디에 사용된 옷들

    var oldXvalue: Float = 0.0f
    var oldYvalue: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi)
        settingToolBar()
        settingAdapter()
        setClickListener()
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
                     if(codiClothesList.contains(data)){
                        Toast.makeText(this@AddCodiActivity, "중복 불가", Toast.LENGTH_SHORT).show()
                    }else{
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
            bottom_rv.adapter = codiBottomRecommendationAdapter
            switchLayoutManager()
        }

        toolbar_codi_bottom.categoryListBtn.setOnClickListener {
            toolbar_codi_bottom.tv_titile.text = "카테고리"
            bottom_rv.adapter = codiBottomCategoryAdapter
            switchLayoutManager()
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