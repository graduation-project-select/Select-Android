package com.konkuk.select.network

import android.util.JsonReader
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.model.*
import org.json.JSONObject

object Fbase {
    val auth: FirebaseAuth = Firebase.auth
    var uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = Firebase.storage

    val USERS_REF = db.collection("users")
    val CLOSETS_REF = db.collection("closets")
    val CLOTHES_REF = db.collection("clothes")
    val CODI_REF = db.collection("codi")
    val CODI_ITEMS_REF = db.collection("codiItems")
    val CODITAG_REF = db.collection("codiTag")
    val CODI_SUGGESTION_REF = db.collection("codiSuggestion")
    val NOTIFICATION_REF = db.collection("notification")    // TODO 이것은 실시간 데이터베이스로 변경하기
    val CODISUG_NOTI_REF = db.collection("codiSugNoti") // codi suggestion notification

    val TEMP_STORAGE_ROOT_NAME = "tempImgs"

    fun getClothes(document:DocumentSnapshot):Clothes{
        val clothesObj = JSONObject(document.data)

        val season = clothesObj.getJSONArray("season")
        val seasonArray = Array(season.length()) { season.getBoolean(it) }

        return Clothes(
            id = document.id,
            category = clothesObj["category"] as String,
            subCategory = clothesObj["subCategory"] as String,
            texture = clothesObj["texture"] as String,
            color_h = clothesObj.getInt("color_h"),
            color_s = clothesObj.getInt("color_s"),
            color_v = clothesObj.getInt("color_v"),
            season = seasonArray.toCollection(ArrayList<Boolean>()),
            imgUri = clothesObj["imgUri"] as String,
            uid = clothesObj["uid"] as String
        )
    }

    fun getCodi(document: DocumentSnapshot):Codi{
        val codiObj = JSONObject(document.data)

        val itemsIds = codiObj.getJSONArray("itemsIds")
        val itemsIdsArray = Array(itemsIds.length()){itemsIds.getString(it)}

        return Codi(
            id = document.id,
            tags = document.get("tags") as ArrayList<DocumentReference>,
            itemsIds = itemsIdsArray.toCollection(ArrayList<String>()),
            public = codiObj["public"] as Boolean,
            date = document.get("date") as Timestamp,
            imgUri = codiObj["imgUri"] as String,
            uid = codiObj["uid"] as String
        )
    }

    fun getCloset(document:DocumentSnapshot):Closet{
        val name = document["name"].toString()
        val count = document["count"].toString().toInt()
        val uid = document["uid"].toString()
        return Closet(
            id = document.id,
            name = name,
            count = count,
            imgUri = "",
            uid = uid
        )
    }

    fun getCodiSuggestion(document: DocumentSnapshot):CodiSuggestion{
        val codiObj = JSONObject(document.data)

        val itemsIds = codiObj.getJSONArray("itemsIds")
        val itemsIdsArray = Array(itemsIds.length()){itemsIds.getString(it)}

        return CodiSuggestion(
            id = document.id,
            itemsIds = itemsIdsArray.toCollection(ArrayList<String>()),
            imgUri = codiObj["imgUri"] as String,
            message = codiObj["message"] as String,
            closetId = codiObj["closetId"] as String,
            ownerUid = codiObj["ownerUid"] as String,
            senderUid = codiObj["senderUid"] as String
        )
    }

    fun getNotification(document: DocumentSnapshot):Notification{
        return Notification(
            id = document.id,
            uid = document.get("uid") as String,
            type = document.get("type") as String,
            notiRef = document.get("notiRef") as DocumentReference
        )
    }

    fun getCodiSugNoti(document: DocumentSnapshot):CodiSugNoti{
        val codiSugNotiObj = JSONObject(document.data)

        val itemsIds = codiSugNotiObj.getJSONArray("codiIds")
        val itemsIdsArray = Array(itemsIds.length()){itemsIds.getString(it)}

        return CodiSugNoti(
            id = document.id,
            codiIds = itemsIdsArray.toCollection(ArrayList<String>()),
            closetId =  document.get("closetId") as String,
            ownerUid =  document.get("ownerUid") as String,
            senderUid =  document.get("senderUid") as String,
            timestamp =  document.get("timestamp") as Timestamp
        )
    }

    fun initUid(){
        uid = auth.currentUser?.uid
    }

    fun signOut(){
        Firebase.auth.signOut()
    }
}