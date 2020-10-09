package com.konkuk.select.activity

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.R
import com.konkuk.select.model.ClothesProp
import com.konkuk.select.model.DefaultResponse
import com.konkuk.select.model.RGBColor
import com.konkuk.select.network.Fbase
import com.konkuk.select.network.RetrofitClient
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

@GlideModule
class AddClothesActivity : AppCompatActivity() {
    private val TAG = "firebase"
    private lateinit var imageFile: File
    var checkedArr: ArrayList<Boolean> = arrayListOf(false, false, false, false)

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
        clothImg.setImageBitmap(rotateImageIfRequired(imageFile.path))
        getClothesAttribute()
    }

    private fun getClothesAttribute() {
        val requestFile: RequestBody =
            imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                var requestFile = ProgressRequestBody(imageFile)
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
                    var category_label = it.category
                    categorySub_tv.text = category_label
                    val hex = java.lang.String.format("#%02x%02x%02x",  it.R, it.G, it.B) // RGB -> #0000 형식으로 변환
                    colorCircle.setBackgroundColor(Color.parseColor(hex));
                }
                Toast.makeText(
                    this@AddClothesActivity,
                    response.body().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<ClothesProp>, t: Throwable) {
                Log.d("Retrofit", "Error: " + t.message)
            }

        })
    }


    private fun settingOnClickListener() {
        addBtn.setOnClickListener {
            uploadImage(imageFile)
            startActivity(Intent(this, MainActivity::class.java))
            // stack 비우기
            finish()
        }

        checkBox_spring.setOnClickListener {
            checkedArr[0] = checkBox_spring.isChecked
        }
        checkBox_summer.setOnClickListener {
            checkedArr[1] = checkBox_summer.isChecked
        }
        checkBox_fall.setOnClickListener {
            checkedArr[2] = checkBox_fall.isChecked
        }
        checkBox_winter.setOnClickListener {
            checkedArr[3] = checkBox_winter.isChecked
        }
    }

    private fun rotateImageIfRequired(imagePath: String): Bitmap? {
        var degrees = 0
        try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
            }
        } catch (e: IOException) {
            Log.e("ImageError", "Error in reading Exif data of $imagePath", e)
        }

        val decodeBounds: BitmapFactory.Options = BitmapFactory.Options()
        decodeBounds.inJustDecodeBounds = true
        var bitmap: Bitmap? = BitmapFactory.decodeFile(imagePath, decodeBounds)
        val numPixels: Int = decodeBounds.outWidth * decodeBounds.outHeight
        val maxPixels = 2048 * 1536 // requires 12 MB heap
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = if (numPixels > maxPixels) 2 else 1
        bitmap = BitmapFactory.decodeFile(imagePath, options)
        if (bitmap == null) {
            return null
        }

        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat())
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true
        )

        return bitmap
    }

    inner class ProgressRequestBody(private val mFile: File) : RequestBody() {

        override fun contentType(): MediaType? {
            return "image/*".toMediaTypeOrNull()
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val fileLength = mFile.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val `in` = FileInputStream(mFile)
            var uploaded: Long = 0
            try {
                var read: Int = 0
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).let { read = it; it != -1 }) {
                    uploaded += read.toLong()
                    sink!!.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }
    }

    private fun uploadImage(file: File) {
        var storageRef = Fbase.storage.reference
        // Create a child reference
        // imagesRef now points to "images"
        var filename = Timestamp.now().nanoseconds.toString() + "_" + file.name
        var imagesRef: StorageReference? =
            Fbase.uid?.let { storageRef.child(it).child(filename) }

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"
        val stream = FileInputStream(file)

        var uploadTask = imagesRef?.putStream(stream)
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(TAG, "it.uploadSessionUri: ${it.uploadSessionUri}")
//            Log.d(TAG, "imagesRef: ${imagesRef}")
            val imgUrl =
                "https://firebasestorage.googleapis.com/v0/b/select-4cfa6.appspot.com/o/${Fbase.uid}%2F${filename}?alt=media"
//            "https://firebasestorage.googleapis.com/v0/b/select-4cfa6.appspot.com/o/9B4rtDTEWwYU7XKSZozL1o2aB9Z2%2F405000000_309466447.png?alt=media"
            insertClothes(
                category_tv.text.toString(),
                categorySub_tv.text.toString(),
                RGBColor(255, 0, 0),
                checkedArr,
                imgUrl.toString()
            )

        }?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }?.addOnPausedListener {
            println("Upload is paused")
        }?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener {
            // Handle successful uploads on complete
            // ...
        }

    }

    private fun insertClothes(
        category: String,
        subCategory: String,
        color: RGBColor,
        checkedArr: ArrayList<Boolean>,
        imgUrl: String
    ) {

        // Create a new user with a first and last name
        val clothes = hashMapOf(
            "uid" to Fbase.uid,
            "category" to category,
            "subCategory" to subCategory,
            "color.R" to color.R,
            "color.G" to color.G,
            "color.B" to color.B,
            "checkedArr" to checkedArr,
            "time" to Timestamp(Date()),
            "imgUrl" to imgUrl
        )
        Log.d(TAG, "insertClothest: ${clothes}")

        // Add a new document with a generated ID
        Fbase.db.collection("clothes")
            .add(clothes)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}