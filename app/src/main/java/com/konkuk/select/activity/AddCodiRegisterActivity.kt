package com.konkuk.select.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import com.konkuk.select.R
import com.konkuk.select.adpater.CodiTagCheckboxListAdapter
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.CodiItem
import com.konkuk.select.model.CodiTag
import com.konkuk.select.network.Fbase
import kotlinx.android.synthetic.main.activity_add_codi_register.*
import kotlinx.android.synthetic.main.toolbar.view.*


class AddCodiRegisterActivity : AppCompatActivity() {
    private val TAG = "DragClickListener"

    lateinit var codiTagCheckboxListAdapter: CodiTagCheckboxListAdapter
    var codiTagList = ArrayList<CodiTag>()

    var checkTagRefArray = arrayListOf<DocumentReference>()

    private lateinit var codiImgByte: ByteArray
    private var codiClothesList = ArrayList<Clothes>()
    private var codiClothesIdList = ArrayList<String>()

    private var isSharing:Boolean = false
    private var ownerUid:String = ""
    private var senderUid:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_codi_register)
        checkSharing()
        getDataFromIntent()
        settingToolBar()
        getTagList()
        settingAdapter()
    }

    private fun checkSharing(){
        if(intent.hasExtra("isSharing") && intent.getBooleanExtra("isSharing", false)){
            isSharing = intent.getBooleanExtra("isSharing", false)
            intent.getStringExtra("ownerUid")?.let{
                ownerUid = it
            }
            intent.getStringExtra("senderUid")?.let{
                senderUid = it
            }
            // TODO 추천시는 태그 선택할 필요 x, 메세지를 첨부할 수 있도록 변경
            // TODO 추천 코디 저장만 하면 됨 (구조 쫌만 더 생각 -> 필터링 어떻게 해야할지)
        }
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
            if(isSharing){
//                uploadImage(codiImgByte, Fbase.TEMP_STORAGE_ROOT_NAME)
            }else{
                Fbase.uid?.let { it1 -> uploadImage(codiImgByte, it1) }
            }
            finish()
            // TODO 방금 올린 코디 상세 페이지로 이동
        }
    }

    private fun getDataFromIntent(){
        intent.getByteArrayExtra("codiImage")?.let {
            codiImgByte = it
            settingCodiImage(codiImgByte)
        }
        (intent.getSerializableExtra("codiClothesList") as ArrayList<Clothes>).let{
            codiClothesList.clear()
            codiClothesList.addAll(it)

            codiClothesIdList.clear()
            for(codi in it){
                codiClothesIdList.add(codi.id)
            }
        }
    }

    private fun settingCodiImage(codiImgByte:ByteArray) {
        val codiImgDecoding = BitmapFactory.decodeByteArray(codiImgByte, 0, codiImgByte!!.size)
        codiDetailImg.setImageBitmap(codiImgDecoding)
    }

    private fun settingAdapter() {
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
                    if(!checkTagRefArray.contains(data.ref)) checkTagRefArray.add(data.ref)
                } else {
                    if(checkTagRefArray.contains(data.ref)) checkTagRefArray.remove(data.ref)
                }
                Log.d(TAG, "checkTagArray: $checkTagRefArray")
            }
        }
    }

    private fun getTagList(){
        Fbase.CODITAG_REF.get().addOnSuccessListener {
            codiTagList.clear()
            for(document in it.documents){
                codiTagList.add(CodiTag(document.reference, document.get("name").toString()))
            }
            codiTagCheckboxListAdapter.notifyDataSetChanged()
        }
    }


    private fun uploadImage(codiImgByte: ByteArray, rootFolderName:String){
        var storageRef = Fbase.storage.reference
        var filename = "codi_"+Timestamp.now().nanoseconds.toString()
        var imagesRef: StorageReference? = storageRef.child(rootFolderName).child("codi").child(filename)

        var uploadTask = imagesRef?.putBytes(codiImgByte)
        uploadTask?.addOnSuccessListener {
            imagesRef?.downloadUrl?.addOnSuccessListener {uri ->
                Log.d(TAG, "downloadUrl: $uri")
                val imgUri = uri.toString()
                val codiObj = CodiRequest(
                    tags = checkTagRefArray,
                    itemsIds = codiClothesIdList,
                    public = open_switch.isChecked,
                    date = Timestamp.now(),
                    imgUri = imgUri,
                    uid = Fbase.auth.uid.toString()
                )
                insetCodi(codiObj)
            }
        }?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.d(TAG, "Upload is $progress% done")
        }?.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }?.addOnFailureListener {
            Log.d(TAG, "Upload is failed")
        }
    }

    data class CodiRequest(
        val tags:ArrayList<DocumentReference>,
        val itemsIds:ArrayList<String>,
        val public:Boolean,
        val date:Timestamp,
        val imgUri:String,
        val uid: String
    )

    // 옷장공유 시 친구가 추천해 준 코디
    data class CodiRcmd(
        val rcmdId:String,
        val itemsIds:ArrayList<String>,
        val imgUri:String,
        val message:String,
        val ownerUid: String,
        val senderUid: String,
        val date:Timestamp
    )
    // TODO 필드 결정하기
    // select -> rcmdId로
    // ownerUid ->
    // senderUid -> 알림 시 누가 추천했는지 알려주기 위해

    private fun insetCodi(codiRequest:CodiRequest){
        Fbase.CODI_REF.add(codiRequest)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                updateUserCodiTagList(codiRequest.tags)
                insertCodiItems(documentReference.id, codiClothesList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun insertCodiItems(codiId:String, codiClothesList:ArrayList<Clothes>){
        for(item in codiClothesList){
            val codiItemObj = CodiItem(
                codiId = codiId,
                clothesId = item.id,
                category = item.category,
                subCategory = item.subCategory,
                texture = item.texture,
                color_h = item.color_h,
                color_s = item.color_s,
                color_v = item.color_v
            )
            Fbase.CODI_ITEMS_REF.add(codiItemObj)
        }

    }

    private fun updateUserCodiTagList(codiTagList:ArrayList<DocumentReference>) {
        Fbase.uid?.let {
            for(tag in codiTagList){
                Fbase.USERS_REF.document(it)
                    .update("codiTagList", FieldValue.arrayUnion(tag))
            }
        }
    }

}