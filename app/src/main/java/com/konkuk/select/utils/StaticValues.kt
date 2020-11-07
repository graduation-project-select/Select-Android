package com.konkuk.select.utils

import com.konkuk.select.model.Category
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object StaticValues {
    val categoryList:ArrayList<String> = arrayListOf("top", "bottom", "dress", "outer", "shoes", "bag", "accessory")

    val categoryListTop:ArrayList<Category> = arrayListOf<Category>(
        Category(0, "top", true),
        Category(1, "bottom", false),
        Category(2, "dress", false),
        Category(3, "outer", false),
        Category(4, "shoes", false),
        Category(5, "bag", false),
        Category(6, "accessory", false)
    )

    val subCategoryList:HashMap<String, ArrayList<String>> = hashMapOf(
        "top" to arrayListOf("blouse", "longTshirt", "shortTshirt", "sleeveless"),
        "bottom" to arrayListOf("longPants", "shortPants", "skirt"),
        "dress" to arrayListOf("dress"),
        "outer" to arrayListOf("cardigan&vest", "coat", "jacket", "jumper"),
        "shoes" to arrayListOf("shoes"),
        "bag" to arrayListOf("bag"),
        "accessory" to arrayListOf("accessory")
    )
}