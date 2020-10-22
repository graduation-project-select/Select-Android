package com.konkuk.select.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import com.konkuk.select.R
import com.konkuk.select.fragment.BottomSheetMainBtnDialog
import com.konkuk.select.fragment.BottomSheetSingleListDialog
import com.konkuk.select.model.ClothesProp
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.RetrofitClient
import com.konkuk.select.utils.ImageManager
import kotlinx.android.synthetic.main.activity_add_clothes.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "AddClothes"

@GlideModule
class AddClothesActivity : AppCompatActivity(),
    BottomSheetSingleListDialog.onChangeCategory {

    private lateinit var imageFile: File

    var season: ArrayList<Boolean> = arrayListOf(false, false, false, false)
    lateinit var clothesRGB:ArrayList<Int>
    lateinit var category:String
    lateinit var subCategory: String
    lateinit var texture:String

    data class ClothesRequest(
        val category: String,
        val subCategory: String,
        val texture:String,
        val color:ArrayList<Int>,
        val season:ArrayList<Boolean>,
        val imgUri:String,
        val uid: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)
        settingToolBar()
        getImgData()
        settingOnClickListener()
    }

    private fun settingToolBar() {
        val leftBtn: ImageView = toolbar.findViewById(R.id.left_iv)
        val title: TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn: ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)
        title.text = "옷 추가하기"
        rightBtn.setImageResource(0)
        rightBtn.visibility = View.INVISIBLE

        leftBtn.setOnClickListener {
            finish()
        }
    }

    private fun getImgData() {
        val currentPhotoPath: String? = intent.getStringExtra("currentPhotoPath")
        imageFile = File(currentPhotoPath)
        clothImg.setImageBitmap(ImageManager.rotateImageIfRequired(imageFile.path))
        getClothesAttribute()
    }

    private fun getClothesAttribute() {
        val requestFile: RequestBody =
            imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        var multipartBody = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            requestFile
        )

        // 서버에서 정보 가져옴
        RetrofitClient.instance.predictClothesProp(imageFile.name, multipartBody).enqueue(object :
            Callback<ClothesProp> {
            override fun onResponse(
                call: Call<ClothesProp>,
                response: Response<ClothesProp>
            ) {
                Log.d("Retrofit", response.body().toString())
                val clothesProp = response.body()
                clothesProp?.let {
                    category = it.category
                    subCategory = it.subCategory
                    texture = it.texture
                    clothesRGB = arrayListOf(it.R, it.G, it.B)
                    initClothesPropView()
                }
                Toast.makeText(
                    this@AddClothesActivity,
                    response.body().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<ClothesProp>, t: Throwable) {
                Log.d("Retrofit", "Error: " + t.message)
                // TODO 디폴트값 설정
                category = "error"
                subCategory = "error"
                texture = "error"
                clothesRGB = arrayListOf(0,0,0)
                initClothesPropView()
            }

        })
    }

    fun initClothesPropView() {
        category_tv.text = category
        categorySub_tv.text = subCategory
        val hex = java.lang.String.format("#%02x%02x%02x",  clothesRGB[0], clothesRGB[1], clothesRGB[2]) // RGB -> #0000 형식으로 변환
        colorCircle.setBackgroundColor(Color.parseColor(hex));
    }

    private fun settingOnClickListener() {
        addBtn.setOnClickListener {
            uploadImage(imageFile)

        }

        checkBox_spring.setOnClickListener {
            season[0] = checkBox_spring.isChecked
        }
        checkBox_summer.setOnClickListener {
            season[1] = checkBox_summer.isChecked
        }
        checkBox_fall.setOnClickListener {
            season[2] = checkBox_fall.isChecked
        }
        checkBox_winter.setOnClickListener {
            season[3] = checkBox_winter.isChecked
        }


        // 카테고리 변경
        category_tv.setOnClickListener {
            val bottomSheetFragment = BottomSheetSingleListDialog(this)
            supportFragmentManager?.let {
                bottomSheetFragment.show(it, bottomSheetFragment.getTag())
            }
        }
    }

    private fun uploadImage(file: File) {
        Log.d(TAG, "이미지 업로드 uid:${Fbase.uid}")

        var storageRef = Fbase.storage.reference
        var filename = Timestamp.now().nanoseconds.toString() + "_" + file.name
        var imagesRef: StorageReference? = Fbase.uid?.let { storageRef.child(it).child("clothes").child(filename) }

        val stream = FileInputStream(file)
        var uploadTask = imagesRef?.putStream(stream)
        uploadTask?.addOnSuccessListener {
            imagesRef?.downloadUrl?.addOnSuccessListener {uri->
                val imgUri = uri.toString()
                var clothesObj = ClothesRequest(category, subCategory, texture, clothesRGB, season, imgUri, Fbase.auth.uid.toString())
                insertClothes(clothesObj)
            }
        }?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.d(TAG, "Upload is $progress% done")
        }?.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }?.addOnFailureListener {
            Log.d(TAG, "firebaseError: ${it.message}")
        }
    }

    private fun insertClothes(clothes: ClothesRequest) {
        Log.d(TAG, "insertClothest: ${clothes}")

        // Add a new document with a generated ID
        Fbase.CLOTHES_REF
            .add(clothes)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    override fun getSelectedCategory(_category: String, _subCategory: String) {
        category = _category
        subCategory = _subCategory
        texture = "none"
        clothesRGB = arrayListOf(0,0,0)

        initClothesPropView()
    }

}