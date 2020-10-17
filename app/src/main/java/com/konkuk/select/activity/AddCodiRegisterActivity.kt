package com.konkuk.select.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagCheckboxListAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.CodiTag
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_add_codi_register.*
import kotlinx.android.synthetic.main.toolbar.view.*


class AddCodiRegisterActivity : AppCompatActivity() {
    private val TAG = "DragClickListener"

    lateinit var codiTagCheckboxListAdapter: CodiTagCheckboxListAdapter
    var codiTagList = ArrayList<CodiTag>()

    var checkTagArray = arrayListOf<String>()
    lateinit var imgUri:String

    private lateinit var codiImgByte: ByteArray
    private var codiClothesList: ArrayList<Clothes> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi_register)
        getDataFromIntent()
        settingToolBar()
        settingAdapter()
    }

    private fun settingToolBar() {
        toolbar.title_tv.text = getString(R.string.activity_title_addCodiRegister)
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "올리기"

        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_tv.setOnClickListener {
            uploadImage(codiImgByte)
            finish()
            // 방금 올린 코디 상세 페이지로 이동
        }
    }

    private fun getDataFromIntent(){
        intent.getByteArrayExtra("codiImage")?.let {
            codiImgByte =it
            settingCodiImage(codiImgByte)
        }
        (intent.getSerializableExtra("codiClothesList") as ArrayList<Clothes>).let{
            codiClothesList.clear()
            codiClothesList.addAll(it)
        }
    }

    private fun settingCodiImage(codiImgByte:ByteArray) {
        val codiImgDecoding = BitmapFactory.decodeByteArray(codiImgByte, 0, codiImgByte!!.size)
        codiDetailImg.setImageBitmap(codiImgDecoding)
    }

    private fun settingAdapter() {
        codiTagList.add(CodiTag("111", "#데이트룩"))
        codiTagList.add(CodiTag("222", "#오피스룩"))
        codiTagList.add(CodiTag("333", "#캠퍼스룩"))
        codiTagList.add(CodiTag("444", "#헬스장갈때"))
        codiTagList.add(CodiTag("555", "#파티룩"))
        codiTagList.add(CodiTag("777", "#바람핀전남친결혼식갈때"))

        codiTag_rv.layoutManager = GridLayoutManager(this, 2)
        codiTagCheckboxListAdapter = CodiTagCheckboxListAdapter(this, codiTagList)
        codiTag_rv.adapter = codiTagCheckboxListAdapter

        val myDisplay = applicationContext.resources.displayMetrics
        val deviceWidth = myDisplay.widthPixels
        // 코디 태그 뷰마다 길이 가져와서 계산하여 화면 width보다 크면 다음줄 작으면 한 줄에 출력 -> 코드 짜기

        codiTagCheckboxListAdapter.itemClickListener = object : CodiTagCheckboxListAdapter.OnItemClickListener {
            override fun OnClickItem(
                holder: CodiTagCheckboxListAdapter.ItemHolder,
                view: View,
                data: CodiTag,
                position: Int
            ) {
                val isChecked = (view as CheckBox).isChecked
                if(isChecked) {
                    if(!checkTagArray.contains(data.tag)) checkTagArray.add(data.tag)
                } else {
                    if(checkTagArray.contains(data.tag)) checkTagArray.remove(data.tag)
                }
                Log.d(TAG, "checkTagArray: $checkTagArray")
            }
        }
    }

    private fun uploadImage(codiImgByte: ByteArray){

        var storageRef = Fbase.storage.reference
        var filename = "codi_"+Timestamp.now().nanoseconds.toString()
        var imagesRef: StorageReference? =
            Fbase.auth.uid?.let { storageRef.child(it).child("codi").child(filename) }

        var uploadTask = imagesRef?.putBytes(codiImgByte)
        uploadTask?.addOnSuccessListener {
            Log.d(TAG, "it.uploadSessionUri: ${it.uploadSessionUri}")
//            val imgUrl = it.uploadSessionUri
            imgUri = "https://firebasestorage.googleapis.com/v0/b/select-4cfa6.appspot.com/o/${Fbase.uid}%2Fcodi%2F${filename}?alt=media"
            val codiObj = CodiRequest(
                tag = checkTagArray,
                items = codiClothesList,
                public = open_switch.isChecked,
                date = Timestamp.now(),
                imgUri = imgUri,
                uid = Fbase.auth.uid.toString()
            )
            insetCodi(codiObj)
        }?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.d(TAG, "Upload is $progress% done")
        }?.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }?.addOnFailureListener {
            Log.d(TAG, "Upload is failed")
        }?.addOnSuccessListener {
            // Handle successful uploads on complete
        }

    }

    data class CodiRequest(
        val tag:ArrayList<String>,
        val items:ArrayList<Clothes>,
        val public:Boolean,
        val date:Timestamp,
        val imgUri:String,
        val uid: String
    )

    private fun insetCodi(codiRequest:CodiRequest){
        // Add a new document with a generated ID
        Fbase.db.collection("codi")
            .add(codiRequest)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}