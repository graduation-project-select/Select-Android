package com.konkuk.select.model

import java.io.Serializable

data class CodiItem (
    val codiId:String,
    val clothesId:String,
    val category:String,
    val subCategory:String,
    val texture:String,
    val color_h:Int,
    val color_s:Int,
    val color_v:Int
): Serializable