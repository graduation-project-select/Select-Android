package com.konkuk.select.utils

import android.graphics.Color

object ColorConverter {
    fun convertRGBtoHSV(colorRGB:IntArray):IntArray{
        var hsv = IntArray(3)
        var hsv_float = FloatArray(3)
        // RGB to HSV
        Color.RGBToHSV(colorRGB[0], colorRGB[1], colorRGB[2], hsv_float)
        hsv[0] = (hsv_float[0] as Number).toInt()
        hsv[1] = ((hsv_float[1]*100) as Number).toInt()
        hsv[2] = ((hsv_float[2]*100) as Number).toInt()
        return hsv
    }

    fun convertHSVtoRGB(colorHSV:IntArray):IntArray{
        var rgb = IntArray(3)
        // HSV to RGB
        var hsv = FloatArray(3)
        hsv[0] = colorHSV[0].toFloat()
        hsv[1] = (colorHSV[1].toFloat())/100
        hsv[2] = (colorHSV[2].toFloat())/100
        var currentColor = Color.HSVToColor(hsv)

        // assign red, green and blue
        rgb[0] = Color.red(currentColor)
        rgb[1] = Color.green(currentColor)
        rgb[2] = Color.blue(currentColor)
        return rgb
    }
}