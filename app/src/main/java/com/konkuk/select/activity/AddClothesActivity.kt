package com.konkuk.select.activity

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
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
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.konkuk.select.R
import com.konkuk.select.model.DefaultResponse
import com.konkuk.select.network.RetrofitClient
import kotlinx.android.synthetic.main.activity_add_cloth.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class AddClothesActivity : AppCompatActivity() {
    lateinit var imageFile:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)
        settingToolBar()
        getImgData()
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
//                var requestFile = ProgressRequestBody(imageFile)
//                var multipartBody = MultipartBody.Part.createFormData("fileToUpload", imageFile.name, requestFile)

                clothImg.setImageBitmap(rotateImageIfRequired(imageFile.path))
                // 서버 업로드
            }
            "CAMERA" -> {
                imageFile = getDataFromCamera()
                clothImg.setImageBitmap(rotateImageIfRequired(imageFile.path))
                // 서버 업로드
            }
        }

    }


    fun getDataFromGallery():File{
        val currentPhotoUri: String? = intent.getStringExtra("currentPhotoUri")
        val photoUri = Uri.parse(currentPhotoUri)
        Log.d("TAG", "photoUri.path: " + photoUri.toString())
        val realPath = getPath(photoUri)
        Log.d("TAG", "realPath: " + realPath)

        val file = File(getPath(photoUri))
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


    inner class ProgressRequestBody(private val mFile:File): RequestBody() {

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
}