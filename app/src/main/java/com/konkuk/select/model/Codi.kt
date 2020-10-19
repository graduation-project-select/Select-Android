package com.konkuk.select.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

//data class Codi(val id:String, val tag: String, val imgUrl:String, val open:Boolean)

data class Codi(
    val id:String,
    val tags:ArrayList<DocumentReference>,
    val items:ArrayList<Clothes>,
    val public:Boolean,
    val date: Timestamp,
    val imgUri:String,
    val uid: String
)