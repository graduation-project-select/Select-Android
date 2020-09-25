package com.konkuk.select.fragment

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.konkuk.select.R
import com.konkuk.select.activity.AddClothesActivity
import kotlinx.android.synthetic.main.fragment_bottom_sheet_image_picker_dialog.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetImagePickerDialog(var ctx: Context) : BottomSheetDialogFragment() {

    val FROM_GALLERY_CODE = 1
    val FROM_CAMERA_CODE = 2
    val REQUEST_CAMERA_PERMISSION = 3

    lateinit var currentPhotoPath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_bottom_sheet_image_picker_dialog,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_galleryBtn.setOnClickListener {
            getPictureFromGallery()
        }
        tv_cameraBtn.setOnClickListener {
            getPictureFromCamera()
        }
    }

    fun finishFragment(){
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FROM_CAMERA_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "카메라 허가 ㅇ")
        }else{
            Log.d("TAG", "카메라 허가 x")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FROM_GALLERY_CODE -> {
                    if(data != null) {
                        Log.d("TAG", "갤러리에서 이미지 선택")
                        var nextIntent = Intent(ctx, AddClothesActivity::class.java)
                        nextIntent.putExtra("type", "GALLERY")
                        nextIntent.putExtra("currentPhotoUri", data?.data.toString())
                        startActivity(nextIntent)
//                        finishFragment()    // 흠
//                    image.setImageURI(data?.data) // handle chosen image
                    }else{
                        Log.d("TAG", "갤러리에서 이미지 선택 x")
                    }
                }
                FROM_CAMERA_CODE ->{
                    Log.d("TAG", "카메라에서 이미지 선택")
                    Log.d("TAG", "currentPhotoPath: ${currentPhotoPath}")
                    var nextIntent = Intent(ctx, AddClothesActivity::class.java)
                    nextIntent.putExtra("type", "CAMERA")
                    nextIntent.putExtra("currentPhotoPath", currentPhotoPath)
                    startActivity(nextIntent)
//                    finishFragment()  // 흠
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun getPictureFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent,FROM_GALLERY_CODE)
    }

    private fun getPictureFromCamera(){
        if (checkPersmission()) {
            dispatchTakePictureIntent()
        } else {
            requestPermission()
        }
    }

    // 카메라 권한 요청
    private fun requestPermission() {
        requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA),
            REQUEST_CAMERA_PERMISSION)
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    // 카메라 열기
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(ctx.packageManager) != null) {
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("TAG", "그림파일 만드는도중 에러생김")
                        null
                    }

                if (Build.VERSION.SDK_INT < 24) {
                    if(photoFile != null){
                        val photoURI = Uri.fromFile(photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, FROM_CAMERA_CODE)
                    }
                }
                else{
                    // 그림파일을 성공적으로 만들었다면 onActivityForResult로 보내기
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            ctx, "com.konkuk.select.fileprovider", it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, FROM_CAMERA_CODE)
                    }
                }
            }
        }
    }

    // 카메라로 촬영한 이미지를 파일로 저장해준다
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("TAG", "timeStamp: ${timeStamp}")
        Log.d("TAG", "storageDir: ${storageDir}")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


}
