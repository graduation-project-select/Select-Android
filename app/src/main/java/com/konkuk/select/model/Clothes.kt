package com.konkuk.select.model

import java.io.Serializable

data class Clothes(
    val id: String,
    val category: String,
    val subCategory: String,
    val texture:String,
    val color_h:Int,
    val color_s:Int,
    val color_v:Int,
    val season:ArrayList<Boolean>,
    val imgUri:String,
    val uid: String
):Serializable
