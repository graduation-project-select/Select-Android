package com.konkuk.select.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object Fbase {
    val auth: FirebaseAuth = Firebase.auth
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = Firebase.storage

}