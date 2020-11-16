package com.konkuk.select.utils

import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import kotlin.math.sqrt

object ZoomUtils:View.OnTouchListener
{
    private val TAG:String = "Touch"
    @SuppressWarnings("unused")
    private val MIN_ZOOM:Float = 1f
    private val MAX_ZOOM:Float = 1f

// These matrices will be used to scale points of the image
    var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()

// The 3 states (events) which the user is trying to perform
    val NONE:Int = 0
    val DRAG:Int = 1
    val ZOOM:Int = 2
    var mode:Int = NONE

// these PointF objects are used to record the point(s) the user is touching
    var start: PointF = PointF()
    var mid:PointF = PointF()
    var oldDist:Float  = 1f

    @Override
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var view:ImageView = v as ImageView;
        view.scaleType = ImageView.ScaleType.MATRIX;
        var scale:Float = 0f

        // Handle touch events here...
        event?.let {
            when(event.action and MotionEvent.ACTION_MASK){
                MotionEvent.ACTION_DOWN -> {
                    // first finger down only
                    savedMatrix.set(matrix);
                    start.set(event.x, event.y);
                    Log.d(TAG, "mode=DRAG (down)")
                    mode = DRAG;
                }
                MotionEvent.ACTION_UP -> { // first finger lifted
                    Log.d(TAG, "mode=DRAG (up)")
                }
                MotionEvent.ACTION_POINTER_UP -> { // second finger lifted
                    mode = NONE;
                    Log.d(TAG, "mode=NONE");
                }
                MotionEvent.ACTION_POINTER_DOWN -> { // first and second finger down
                    oldDist = spacing(event);
                    Log.d(TAG, "oldDist=" + oldDist)
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM (down)");
                    }else{
                        Log.e(TAG, "else 1")
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                    } else if (mode == ZOOM) {
                        // pinch zooming
                        var newDist:Float = spacing (event);
                        Log.d(TAG, "newDist=" + newDist);
                        if(newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist; // setting the scaling of the
                            // matrix...if scale > 1 means
                            // zoom in...if scale < 1 means
                            // zoom out
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            Log.e(TAG, "scale, scale, mid.x, mid.y: ${scale}, ${scale}, ${mid.x}, ${mid.y}")
                        }else{
                            Log.e(TAG, "else 2")
                        }
                    }else{
                        Log.e(TAG, "else 2")
                    }
                }
                else -> {}
            }
        }

        view.imageMatrix = matrix; // display the transformation on screen
        return true; // indicate event was handled
    }



    private fun spacing(event:MotionEvent):Float
    {
        var x:Float = event.getX(0) - event.getX(1);
        var y:Float = event.getY(0) - event.getY(1);
        return sqrt(x * x + y * y)
    }


    private fun midPoint( point:PointF,  event: MotionEvent)
    {
        var x:Float = event.getX(0) + event.getX(1);
        var y:Float = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    fun initZoomUtils(v:View){

    }

}