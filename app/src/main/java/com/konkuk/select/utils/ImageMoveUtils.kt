package com.konkuk.select.utils

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.sqrt

class ImageMoveUtils() {
    private var X = 0f
    private var Y = 0f
    private var width = 0f
    private var height = 0f

    var oldXvalue: Float = 0.0f
    var oldYvalue: Float = 0.0f

    // 핀치시 두좌표간의 거리 저장
    var oldDist: Float = 1f
    var newDist: Float = 1f

    // 드래그 모드인지 핀치줌 모드인지 구분
    val NONE: Int = 0
    val DRAG: Int = 1
    val ZOOM: Int = 2

    var mode: Int = NONE

    fun TouchProcess(v: View?, event: MotionEvent?) {
        width = ((v?.parent as ViewGroup).width - v.width).toFloat()
        height = ((v?.parent as ViewGroup).height - v.height).toFloat()
        val act = event!!.action
        when (act and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> if (InObject(event.x, event.y)) { //손가락 터치 위치가 이미지 안에 있으면 DragMode가 시작된다.
                oldXvalue = event.x
                oldYvalue = event.y
                mode = DRAG
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {   // 드래그 중이면, 이미지의 X,Y값을 변환시키면서 위치 이동.
                v.x = event.rawX - oldXvalue
                v.y = event.rawY - (oldYvalue + v.height / 2)
                if (v.x > width && v.y > height) {
                    v.x = width
                    v.y = height
                } else if (v.x < 0 && v.y > height) {
                    v.x = 0f
                    v.y = height
                } else if (v.x > width && v.y < 0) {
                    v.x = width
                    v.y = 0f
                } else if (v.x < 0 && v.y < 0) {
                    v.x = 0f
                    v.y = 0f
                } else if (v.x < 0 || v.x > width) {
                    if (v.x < 0) {
                        v.x = 0f
                        v.y = event.rawY - oldYvalue - v.height
                    } else {
                        v.x = width.toFloat()
                        v.y = event.rawY - oldYvalue - v.height
                    }
                } else if (v.y < 0 || v.y > height) {
                    if (v.y < 0) {
                        v.x = event.rawX - oldXvalue
                        v.y = 0f
                    } else {
                        v.x = event.rawX - oldXvalue
                        v.y = height
                    }
                }
            } else if (mode == ZOOM) {    // 핀치줌 중이면, 이미지의 거리를 계산해서 확대를 한다.
                newDist = spacing(event)
                if (newDist - oldDist > 30) {  // zoom in
                    Log.d("event", "zoom in")
                    val scale: Float = sqrt((newDist - oldDist) * (newDist - oldDist) / (height * height + width * width))
                    v.scaleX = v.scaleX * scale
                    v.scaleY = v.scaleY * scale
                    v.x = v.x * scale
                    v.y = v.y * scale
                    oldDist = newDist
                } else if (oldDist - newDist > 30) {  // zoom out
                    Log.d("event", "zoom out")
                    var scale: Float = sqrt((newDist - oldDist) * (newDist - oldDist) / (height * height + width * width))
                    v.scaleX = v.scaleX / scale
                    v.scaleY = v.scaleY / scale
                    v.x = v.x / scale
                    v.y = v.y / scale
                    oldDist = newDist
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            MotionEvent.ACTION_POINTER_DOWN -> mode = ZOOM
        }
    }

    fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    fun InObject(eventX: Float, eventY: Float): Boolean {
        if ((eventX < (X + width + 30)) && (eventX > X - 30) && (eventY < Y + height + 30) && (eventY > Y - 30)) {
            return true;
        }
        return false;
    }

}