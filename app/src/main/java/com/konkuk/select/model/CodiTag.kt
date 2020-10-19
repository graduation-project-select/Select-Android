package com.konkuk.select.model

import com.google.firebase.firestore.DocumentReference

data class CodiTag(val ref:DocumentReference, val tag:String)