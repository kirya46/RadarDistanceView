package com.common.radardistanceview

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.view.View


/**
 * Created by Kirill Stoianov on 05/09/18.
 */
class RadarDistanceView(context: Context) : View(context) {

    private var gradientStrokePaint: Paint = Paint()
    private var linePaint: Paint = Paint()
    private val gradientStrokeWidth = 15F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()

        drawImage(width, height, canvas)
        drawLines(width, height, canvas)
        drawGradientStroke(width, height, canvas)
    }

    private fun drawImage(width: Float, height: Float, canvas: Canvas) {
        val circleRadius = height / 3

        //get aspect ratio
        val aspRat = if (width > height) {
            (width / 2) / (height / 2)
        } else {
            (height / 2) / (width / 2)
        }

        val bitmapWith: Int
        val bitmapHeight: Int
        if (width < height) {
            bitmapWith = (width * aspRat).toInt() / 2
            bitmapHeight = height.toInt() / 2
        } else {
            bitmapHeight = (height * aspRat).toInt() / 2
            bitmapWith = width.toInt() / 2
        }

        //scale the bitmap
        val resource = BitmapFactory.decodeResource(context.resources, R.drawable.img_kasha_radar)
        val bitmap = Bitmap.createScaledBitmap(resource, bitmapWith, bitmapHeight, false)

        //make circle
        val circularBitmap = Util.getCircularBitmap(bitmap, radius = (circleRadius - gradientStrokeWidth))

        canvas.drawBitmap(circularBitmap, width / 2 - (circleRadius - gradientStrokeWidth), height / 2 - (circleRadius - gradientStrokeWidth), linePaint)
    }

    private fun drawLines(width: Float, height: Float, canvas: Canvas) {
        val lineColor: Int = ContextCompat.getColor(context, R.color.colorLine)
        linePaint.color = lineColor
        linePaint.style = Paint.Style.FILL_AND_STROKE
        linePaint.strokeWidth = 4f
        canvas.drawLine(width / 2, 0f, width / 2, height, linePaint)
        canvas.drawLine(0f, height / 2, width, height / 2, linePaint)
    }

    private fun drawGradientStroke(width: Float, height: Float, canvas: Canvas) {
        val shadowColor: Int = Color.TRANSPARENT
        val violetColor: Int = Color.parseColor("#d116f5")
        val blueColor: Int = Color.parseColor("#4149e6")
        val colorStateList: IntArray = intArrayOf(shadowColor, blueColor, shadowColor, violetColor, shadowColor)
        val shader = SweepGradient((width / 2), height / 2, colorStateList, floatArrayOf(0f, .2f, .5f, .7f, 1f))

        gradientStrokePaint.color = Color.BLACK
        gradientStrokePaint.strokeWidth = gradientStrokeWidth
        gradientStrokePaint.style = Paint.Style.STROKE
        gradientStrokePaint.shader = shader

        val radius = height / 3

        canvas.drawCircle(width / 2, height / 2, radius, gradientStrokePaint)
    }

}