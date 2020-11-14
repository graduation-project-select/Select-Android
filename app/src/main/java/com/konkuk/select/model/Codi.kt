package com.konkuk.select.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Codi(
    val id:String,
    val tags:ArrayList<DocumentReference>,
    val itemsIds:ArrayList<String>,
    val public:Boolean,
    val timestamp: Long,
    val year: Int,
    val month: Int,
    val date: Int,
    val imgUri:String,
    val uid: String
): Serializable