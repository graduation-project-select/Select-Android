package com.konkuk.select.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ImageManager {

    companion object {
        fun rotateImageIfRequired(imagePath: String): Bitmap? {
            var degrees = 0
            try {
                val exif = ExifInterface(imagePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
                }
            } catch (e: IOException) {
                Log.e("ImageError", "Error in reading Exif data of $imagePath", e)
            }

            val decodeBounds: BitmapFactory.Options = BitmapFactory.Options()
            decodeBounds.inJustDecodeBounds = true
            var bitmap: Bitmap? = BitmapFactory.decodeFile(imagePath, decodeBounds)
            val numPixels: Int = decodeBounds.outWidth * decodeBounds.outHeight
            val maxPixels = 2048 * 1536 // requires 12 MB heap
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = if (numPixels > maxPixels) 2 else 1
            bitmap = BitmapFactory.decodeFile(imagePath, options)
            if (bitmap == null) {
                return null
            }

            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true
            )

            return bitmap
        }

    }

    inner class ProgressRequestBody(private val mFile: File) : RequestBody() {

        override fun contentType(): MediaType? {
            return "image/*".toMediaTypeOrNull()
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val fileLength = mFile.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val `in` = FileInputStream(mFile)
            var uploaded: Long = 0
            try {
                var read: Int = 0
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).let { read = it; it != -1 }) {
                    uploaded += read.toLong()
                    sink!!.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }
    }

}