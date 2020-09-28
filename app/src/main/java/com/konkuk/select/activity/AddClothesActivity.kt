package com.konkuk.select.activity

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.R
import com.konkuk.select.model.DefaultResponse
import com.konkuk.select.model.RGBColor
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
import kotlin.collections.ArrayList

class AddClothesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private var storage = Firebase.storage
    val TAG = "firebase"
    lateinit var imageFile:File
    var checkedArr:ArrayList<Boolean> = arrayListOf(false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)
        auth = Firebase.auth
        settingToolBar()
        getImgData()
        settingOnClickListener()

    }

    fun settingToolBar(){
        // ToolBar 변경하는 코드
        val leftBtn : ImageView = toolbar.findViewById(R.id.left_iv)
        val title : TextView = toolbar.findViewById(R.id.title_tv)
        val rightBtn : ImageView = toolbar.findViewById(R.id.right_iv)

        leftBtn.setImageResource(R.drawable.back)
        title.text = "옷 추가하기"
        rightBtn.setImageResource(0)

        leftBtn.setOnClickListener {
            finish()
        }
    }

    fun getImgData(){
        val type: String? = intent.getStringExtra("type")
        when(type){
            "GALLERY" -> {
                imageFile = getDataFromGallery()
                val requestFile: RequestBody =
                    imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                var requestFile = ProgressRequestBody(imageFile)
                var multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestFile
                )

                clothImg.setImageBitmap(rotateImageIfRequired(imageFile.path))
                val hex =
                    java.lang.String.format("#%02x%02x%02x", 255, 0, 0) // RGB -> #0000 형식으로 변환
                colorCircle.setBackgroundColor(Color.parseColor(hex));
                // 서버에서 정보 가져옴
                RetrofitClient.instance.postImage(imageFile.name, multipartBody).enqueue(object :
                    Callback<DefaultResponse> {
                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
//                        Log.d("Retrofit", response.body()!!.toString())
                        Toast.makeText(
                            this@AddClothesActivity,
                            response.body().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Log.d("Retrofit", "Error: " + t.message)

                    }

                })
//                RetrofitClient.instance.predictClothesProp().enqueue(object :
//                    Callback<ClothesProp> {
//                    override fun onFailure(call: Call<ClothesProp>, t: Throwable) {
//                        t.message?.let { Log.d("Retrofit", it) }
//                    }
//
//                    override fun onResponse(
//                        call: Call<ClothesProp>,
//                        response: Response<ClothesProp>
//                    ) {
//                        Log.d("Retrofit", "success: " + response.body()!!.success.toString())
//                        Toast.makeText(
//                            this@AddClothesActivity,
//                            response.body().toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                })
            }
            "CAMERA" -> {
                imageFile = getDataFromCamera()
                clothImg.setImageBitmap(rotateImageIfRequired(imageFile.path))
                // 서버 업로드
            }
        }

    }

    fun settingOnClickListener(){
        addBtn.setOnClickListener {
            Toast.makeText(this, "등록", Toast.LENGTH_SHORT).show()
            uploadImage(imageFile)
////            var colorId = (colorCircle.background as ColorDrawable).color
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



    fun getDataFromGallery():File{
        val currentPhotoUri: String? = intent.getStringExtra("currentPhotoUri")
        val photoUri = Uri.parse(currentPhotoUri)
        Log.d("TAG", "photoUri.path: " + photoUri.toString())
        val realPath = getPath(photoUri)
        Log.d("TAG", "realPath: " + realPath)

        val file = File(realPath)
        return file
    }


    private fun getPath(uri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, uri, proj, null, null, null)
        loader.loadInBackground()?.let {
            val cursor: Cursor = it
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
        return ""
    }

    fun getDataFromCamera():File{
        val currentPhotoPath: String? = intent.getStringExtra("currentPhotoPath")
        // 카메라로부터 받은 데이터가 있을경우에만
        val file = File(currentPhotoPath)
//        if (Build.VERSION.SDK_INT < 28) {
//            val bitmap = MediaStore.Images.Media
//                .getBitmap(contentResolver, Uri.fromFile(file))  //Deprecated
//            clothImg.setImageBitmap(bitmap)
//        }
//        else{
//            val decode = ImageDecoder.createSource(contentResolver,
//                Uri.fromFile(file))
//            val bitmap = ImageDecoder.decodeBitmap(decode)
//            clothImg.setImageBitmap(bitmap)
//        }
//        clothImg.setImageBitmap(rotateImageIfRequired(file.path))
        return file
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

    inner class ProgressRequestBody(private val mFile: File): RequestBody() {

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
            var uploaded:Long = 0
            try{
                var read:Int = 0
                val handler = Handler(Looper.getMainLooper())
                while(`in`.read(buffer).let { read = it ; it != -1 }){
                    uploaded += read.toLong()
                    sink!!.write(buffer, 0, read)
                }
            }finally{
                `in`.close()
            }
        }
    }

    private fun uploadImage(file: File){
        var storageRef = storage.reference
        // Create a child reference
        // imagesRef now points to "images"
        var imagesRef: StorageReference? =
            auth.uid?.let { storageRef.child(it).child(Timestamp.now().nanoseconds.toString() + "_" + file.name) }

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
            val imgUrl = it.uploadSessionUri

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
    ){

        // Create a new user with a first and last name
        val clothes = hashMapOf(
            "uid" to auth.uid,
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
        db.collection("clothes")
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