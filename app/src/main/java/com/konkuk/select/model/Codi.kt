package com.konkuk.select.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Codi(
    val id:String,
    val tags:ArrayList<DocumentReference>,  // TODO 이거 때문에 Serializable 안돼나? Parcelable로 할수있나 찾아보기
    val itemsIds:ArrayList<String>,
    val public:Boolean,
    val date: Timestamp,
    val imgUri:String,
    val uid: String
): Serializable