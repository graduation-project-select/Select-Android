package com.konkuk.select.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagCheckboxListAdapter
import com.konkuk.select.model.CodiTag
import kotlinx.android.synthetic.main.activity_add_codi_register.*
import kotlinx.android.synthetic.main.activity_add_codi_register.toolbar
import kotlinx.android.synthetic.main.toolbar.view.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList


class AddCodiRegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private var storage = Firebase.storage
    private val TAG = "DragClickListener"

    lateinit var codiTagCheckboxListAdapter: CodiTagCheckboxListAdapter
    var codiTagList = ArrayList<CodiTag>()
    var checkTagArray = mutableMapOf<String, Boolean>()
    lateinit var codiImgDecoding:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi_register)
        setToolBar()
        setCodiImage()
        settingAdapter()

    }

    fun setToolBar() {
        toolbar.title_tv.text = getString(R.string.activity_title_addCodiRegister)
        toolbar.left_iv.setImageResource(R.drawable.back)
        toolbar.right_iv.visibility = View.GONE
        toolbar.right_tv.visibility = View.VISIBLE
        toolbar.right_tv.text = "올리기"

        toolbar.left_iv.setOnClickListener {
            finish()
        }
        toolbar.right_tv.setOnClickListener {
            //uploadImage()
            finish()
            // 방금 올린 코디 상세 페이지로 이동
        }
    }

    fun setCodiImage() {
        val codiImgByte = intent.getByteArrayExtra("codiImage")
        codiImgDecoding = BitmapFactory.decodeByteArray(codiImgByte, 0, codiImgByte!!.size)
        codiDetailImg.setImageBitmap(codiImgDecoding)
    }

    fun settingAdapter() {
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
                    checkTagArray.put(data.tag, isChecked)
                } else {
                    checkTagArray.remove(data.tag)
                }
            }
        }
    }

    fun uploadImage(file: File){
        var codiTag:String = ""
        var codiImg = codiDetailImg
        var openState = open_switch.isChecked

        for(checkStatus in checkTagArray.keys) {
            codiTag = codiTag + " " +checkStatus
        }

        var storageRef = storage.reference
        // Create a child reference
        // imagesRef now points to "images"
        var imagesRef: StorageReference? =
            auth.uid?.let { storageRef.child(it).child(Timestamp.now().nanoseconds.toString()+"_"+file.name) }

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

            insertCodi(codiTag, imgUrl.toString(), openState)

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

    fun insertCodi(tag:String, imgUrl:String, open:Boolean) {
        // Create a new user with a first and last name
        val codi = hashMapOf(
            "uid" to auth.uid,
            "tag" to tag,
            "time" to Timestamp(Date()),
            "imgUrl" to imgUrl
        )
        Log.d(TAG, "insertClothest: ${codi}")

        // Add a new document with a generated ID
        db.collection("clothes")
            .add(codi)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }
}