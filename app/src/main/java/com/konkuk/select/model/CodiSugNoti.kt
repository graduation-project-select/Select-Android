package com.konkuk.select.model

import java.security.Timestamp

data class CodiSugNoti(
    val id:String,
    val codiIds:ArrayList<String>,
    val closetId:String,
    val ownerUid:String,
    val senderUid:String,
    val timestamp: Timestamp
)