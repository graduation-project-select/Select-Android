package com.konkuk.select.activity

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.konkuk.select.R
import com.konkuk.select.fragment.BottomSheetSingleListDialog
import com.konkuk.select.model.ClothesProp
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.RetrofitClient
import com.konkuk.select.utils.ColorConverter
import com.konkuk.select.utils.ImageManager
import kotlinx.android.synthetic.main.activity_add_clothes.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

private const val TAG = "AddClothes"

@GlideModule
class AddClothesActivity : AppCompatActivity(),
    BottomSheetSingleListDialog.onChangeCategory {

    private lateinit var imageFile: File

    var season: ArrayList<Boolean> = arrayListOf(false, false, false, false)
    var colorRGB = IntArray(3)
    lateinit var category: String
    lateinit var subCategory: String
    lateinit var texture: String
    var imageByteArray: ByteArray? = null

    data class ClothesRequest(
        val category: String,
        val subCategory: String,
        val texture: String,
        val color_h: Int,
        val color_s: Int,
        val color_v: Int,
        val season: ArrayList<Boolean>,
        val imgUri: String,
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
        clothesImg.setImageBitmap(ImageManager.rotateImageIfRequired(imageFile.path))
        getClothesAttribute()
    }

    var tryCount = 3
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
                Log.d(TAG, response.body().toString())

                val clothesProp = response.body()
                clothesProp?.let {
                    if(it.success){
                        category = it.category
                        subCategory = it.subCategory
                        texture = it.texture
                        colorRGB[0] = it.R
                        colorRGB[1] = it.G
                        colorRGB[2] = it.B
                        imageByteArray = Base64.decode(it.encodedImage, 0)
                        Log.d(TAG, "imageByteArray: $imageByteArray")
                        initClothesPropView()
                        imageByteArray?.let { setClothesImage(it) }
                    }else{
                        Log.e(TAG, "success: False")
                        onFailureGetClothesProps()
                    }
                }
            }

            override fun onFailure(call: Call<ClothesProp>, t: Throwable) {
                Log.e(TAG, "Error: " + t.message)
                onFailureGetClothesProps()
            }

        })
    }

    fun onFailureGetClothesProps(){
        if(tryCount > 0){   // 실패시 3번 더 요청
            Log.e(TAG, "시도 횟수: $tryCount")
            getClothesAttribute()
            tryCount--
        }else{
            // TODO 디폴트값 설정
            category = " - "
            subCategory = " - "
            texture = "none"
            colorRGB[0] = 0
            colorRGB[1] = 0
            colorRGB[2] = 0
            initClothesPropView()
        }
    }

    fun initClothesPropView() {
        category_tv.text = category
        categorySub_tv.text = subCategory
        val hex = java.lang.String.format("#%02x%02x%02x", colorRGB[0], colorRGB[1], colorRGB[2]) // RGB -> #0000 형식으로 변환
        colorCircle.setBackgroundColor(Color.parseColor(hex));
    }

    private fun setClothesImage(imageByteArray: ByteArray) {
        Log.d(TAG, "setClothesImage")
        imageByteArray?.let{
            val clothesImageDecoding = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            clothesImg.setImageBitmap(clothesImageDecoding)
        }
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
        all_check_tv.setOnClickListener {
            checkBox_spring.isChecked = true
            checkBox_summer.isChecked = true
            checkBox_fall.isChecked = true
            checkBox_winter.isChecked = true
            for ((i, s) in season.withIndex()) season[i] = true
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

        var uploadTask =
            if (imageByteArray != null) imageByteArray?.let{ imagesRef?.putBytes(it) }
            else imagesRef?.putStream(stream)

        uploadTask?.addOnSuccessListener {
            imagesRef?.downloadUrl?.addOnSuccessListener { uri ->
                val imgUri = uri.toString()
                val colorHSV = ColorConverter.convertRGBtoHSV(colorRGB)

                var clothesObj = ClothesRequest(
                    category = category,
                    subCategory = subCategory,
                    texture = texture,
                    color_h = colorHSV[0],
                    color_s = colorHSV[1],
                    color_v = colorHSV[2],
                    season = season,
                    imgUri = imgUri,
                    uid = Fbase.auth.uid.toString()
                )
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
//        texture = "none"
//        colorRGB[0] = 0
//        colorRGB[1] = 0
//        colorRGB[2] = 0

        initClothesPropView()
    }

}