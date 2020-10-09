package com.konkuk.select.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

object Fbase {
    val auth: FirebaseAuth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

}