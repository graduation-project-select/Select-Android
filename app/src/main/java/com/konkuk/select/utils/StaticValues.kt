package com.konkuk.select.utils

import com.konkuk.select.model.Category
import java.util.*
import kotlin.collections.ArrayList

object StaticValues {
    val categoryList:ArrayList<String> = arrayListOf("top", "bottom", "dress", "outer", "shoes", "accessory")

    val categoryListTop:ArrayList<Category> = arrayListOf<Category>(
        Category(0, "top", true),
        Category(1, "bottom", false),
        Category(2, "dress", false),
        Category(3, "outer", false),
        Category(4, "shoes", false),
        Category(5, "accessory", false)
    )
}