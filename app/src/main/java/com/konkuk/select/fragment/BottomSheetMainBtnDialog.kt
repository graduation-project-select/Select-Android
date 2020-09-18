package com.konkuk.select.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.select.R
import com.konkuk.select.activity.AddCodiActivity
import kotlinx.android.synthetic.main.fragment_bottom_sheet_mainbtn_dialog.*

class BottomSheetMainBtnDialog(var ctx: Context) : BottomSheetDialogFragment() {

    val FROM_GALLERY_CODE = 1
    val FROM_CAMERA_CODE = 2
    val REQUEST_CAMERA_PERMISSION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_mainbtn_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_addClothesBtn.setOnClickListener {
//            Toast.makeText(ctx, "옷 등록", Toast.LENGTH_SHORT).show()
            // TODO: 카메라, 갤러리 선택 팝업 또는 bottom sheet 띄워야 함
            getPictureFromGallery()
            finishFragment()
        }

        tv_addCodiBtn.setOnClickListener {
//            Toast.makeText(ctx, "코디 등록", Toast.LENGTH_SHORT).show()
            val nextIntent = Intent(ctx, AddCodiActivity::class.java)
            startActivity(nextIntent)
            finishFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FROM_GALLERY_CODE -> {
                    if(data != null){
//                        selectedImageUri = data.data
//                        selectedImageUri?.let {
//                            if (!it.path?.isEmpty()!!){
//                                var fileName = "img_${(System.currentTimeMillis() / 1000)}.jpg"
//                                fileUploader = FileUploader(this, it, fileName)
//                                iconCamera.scaleType = ImageView.ScaleType.CENTER_CROP
//                                iconCamera.setImageURI(fileUploader!!.imageFile.toUri())
//                                setProfileImgCheck("camera", iconCameraCV)
//                            }
//                        }
                    }else{
                        Toast.makeText(ctx, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                FROM_CAMERA_CODE ->{
//                    selectedImageUri?.let {
////                        var fileName = it.pathSegments.last()
//                        var fileName = "img_${(System.currentTimeMillis() / 1000)}.jpg"
//                        fileUploader = FileUploader(this, it, fileName)
//                        iconCamera.scaleType = ImageView.ScaleType.CENTER_CROP
//                        iconCamera.setImageURI(fileUploader!!.imageFile.toUri())
//                        setProfileImgCheck("camera", iconCameraCV)
//                    }
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

    fun finishFragment(){
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

}