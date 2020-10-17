package com.konkuk.select.model

import java.io.Serializable

data class Clothes(
    val id: String,
    val category: String,
    val subCategory: String,
    val texture:String,
    val color: ArrayList<Int>,
    val season:ArrayList<Boolean>,
    val imgUri:String,
    val uid: String
):Serializable
