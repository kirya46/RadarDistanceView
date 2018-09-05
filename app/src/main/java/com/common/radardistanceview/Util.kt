package com.common.radardistanceview

import android.graphics.*


/**
 * Created by Kirill Stoianov on 06/09/18.
 *
 * @link https://stackoverflow.com/questions/11932805/cropping-circular-area-from-bitmap-in-android
 */
class Util {
    companion object {

        fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
            val output = Bitmap.createBitmap(bitmap.width,
                    bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f,
                    bitmap.width / 2f, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
            //return _bmp;
            return output
        }

        fun getCircularBitmap(bitmap: Bitmap): Bitmap {
//
//            val output: Bitmap = if (bitmap.width > bitmap.height) {
//                Bitmap.createBitmap(bitmap.height, bitmap.height, Bitmap.Config.ARGB_8888)
//            } else {
//                Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)
//            }
//
//            val canvas = Canvas(output)
//
//            val color = -0xbdbdbe
//            val paint = Paint()
//            val rect = Rect(0, 0, bitmap.width, bitmap.height)
//
//            val r: Float = if (bitmap.width > bitmap.height) {
//                (bitmap.height / 2).toFloat()
//            } else {
//                (bitmap.width / 2).toFloat()
//            }
//
//            paint.isAntiAlias = true
//            canvas.drawARGB(0, 0, 0, 0)
//            paint.color = color
//            canvas.drawCircle(r, r, r, paint)
//            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//            canvas.drawBitmap(bitmap, rect, rect, paint)
//            return output

            val r: Float = if (bitmap.width > bitmap.height) {
                (bitmap.height / 2).toFloat()
            } else {
                (bitmap.width / 2).toFloat()
            }

            return getCircularBitmap(bitmap, r)
        }

        fun getCircularBitmap(bitmap: Bitmap, radius: Float): Bitmap {

            val output: Bitmap = if (bitmap.width > bitmap.height) {
                Bitmap.createBitmap(bitmap.height, bitmap.height, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(output)

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(radius, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}