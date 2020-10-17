package com.konkuk.select.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.konkuk.select.model.Clothes
import org.json.JSONObject

object Fbase {
    val auth: FirebaseAuth = Firebase.auth
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = Firebase.storage


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
}