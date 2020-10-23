package com.konkuk.select.model

import com.google.firebase.Timestamp

// 옷장공유 시 친구가 추천해 준 코디
data class CodiSuggestion(
    val id:String,
    val itemsIds:ArrayList<String>,
    val imgUri:String,
    val message:String,
    val closetId:String,
    val ownerUid:String,
    val senderUid:String
)