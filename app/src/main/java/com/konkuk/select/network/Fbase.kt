package com.konkuk.select.network

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.model.Clothes
import com.konkuk.select.model.Codi
import org.json.JSONObject

object Fbase {
    val auth: FirebaseAuth = Firebase.auth
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = Firebase.storage

    val USERS_REF = db.collection("users")
    val CLOSETS_REF = db.collection("closets")
    val CLOTHES_REF = db.collection("clothes")
    val CODI_REF = db.collection("codi")
    val CODITAG_REF = db.collection("codiTag")

    fun getClothes(document:QueryDocumentSnapshot):Clothes{
        val clothesObj = JSONObject(document.data)

        val color = clothesObj.getJSONArray("color")
        val colorArray = Array(color.length()) { color.getInt(it) }
        val season = clothesObj.getJSONArray("season")
        val seasonArray = Array(season.length()) { season.getBoolean(it) }

        return Clothes(
            id = document.id,
            category = clothesObj["category"] as String,
            subCategory = clothesObj["subCategory"] as String,
            texture = clothesObj["texture"] as String,
            color = colorArray.toCollection(ArrayList<Int>()),
            season = seasonArray.toCollection(ArrayList<Boolean>()),
            imgUri = clothesObj["imgUri"] as String,
            uid = clothesObj["uid"] as String
        )
    }

    fun getCodi(document: QueryDocumentSnapshot):Codi{
        val codiObj = JSONObject(document.data)

        val tags = codiObj.getJSONArray("tags")
//        val tagsArray = Array(tags.length()) {tags.get(it)} as Array<DocumentReference>
        val items = codiObj.getJSONArray("items")
//        val itemsArray = Array(items.length()){items.get(it)} as Array<Clothes>
        return Codi(
            id = document.id,
            tags = arrayListOf(),   // TODO
            items = arrayListOf(),  // TODO
            public = codiObj["public"] as Boolean,
            date = Timestamp.now(),
//            date = codiObj["date"] as Timestamp,
            imgUri = codiObj["imgUri"] as String,
            uid = codiObj["uid"] as String
        )
    }
}