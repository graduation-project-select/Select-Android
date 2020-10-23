package com.konkuk.select.model

import com.google.firebase.firestore.DocumentReference

data class Notification(
    val id:String,
    val uid: String,
    val type:String,
    val notiRef:DocumentReference
)