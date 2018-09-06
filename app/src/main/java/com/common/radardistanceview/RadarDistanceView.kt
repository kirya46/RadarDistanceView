package com.common.radardistanceview

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View


/**
 * Created by Kirill Stoianov on 05/09/18.
 */
class RadarDistanceView(context: Context, attributeSet: AttributeSet?, deff: Int) : View(context, attributeSet, deff) {

    companion object {
        val TAG: String = RadarDistanceView::class.java.simpleName
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var scaleFactor = 0.0f

    private val gradientStrokeWidth = 15F

    private val resourceBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.img_kasha_radar)
    }

    private val mainGradientStrokePaint by lazy {
        Paint().apply {
            val shadowColor: Int = Color.TRANSPARENT
            val violetColor: Int = Color.parseColor("#d116f5")
            val blueColor: Int = Color.parseColor("#4149e6")
            val colorStateList: IntArray = intArrayOf(shadowColor, blueColor, shadowColor, violetColor, shadowColor)
            val mShader = SweepGradient(width / 2F, height / 2F, colorStateList, floatArrayOf(0f, .2f, .5f, .7f, 1f))

            isAntiAlias = true
            color = Color.BLACK
            shader = mShader
        }
    }

    private val linePaint by lazy {
        Paint().apply {
            val lineColor: Int = ContextCompat.getColor(context, R.color.colorLine)
            color = lineColor
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 4f
        }
    }

    private val pulsingCirclePaint by lazy {
        Paint().apply {
            val lineColor: Int = ContextCompat.getColor(context, R.color.colorLine)
            color = lineColor
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 4f
        }
    }

    private val transparentCirclePaint by lazy {
        Paint().apply {
            color = Color.TRANSPARENT
            isAntiAlias = true
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(Math.min(measuredWidth, measuredHeight), Math.min(measuredWidth, measuredHeight))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGradientStroke(canvas)
        drawImage(canvas)
    }

    fun setScaleFactor(value: Float) {
        scaleFactor = value
        invalidate()
    }

    private fun drawGradientStroke(canvas: Canvas) {
        val width: Float = canvas.width.toFloat()
        val height: Float = canvas.height.toFloat()
        val radius = Math.min(width, height) / 2
        canvas.drawCircle(width / 2, height / 2, radius, mainGradientStrokePaint)
        canvas.drawCircle(width / 2, height / 2, radius - gradientStrokeWidth, transparentCirclePaint)
    }

    private fun drawImage(mainCanvas: Canvas) {

        val size = (Math.min(mainCanvas.width, mainCanvas.height) - gradientStrokeWidth)

        val output = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)

        val color = Color.CYAN
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        val originalWidth = resourceBitmap.width
        val originalHeight = resourceBitmap.height

        val scaleX = (size / originalWidth) + scaleFactor
        val scaleY = (size / originalHeight) + scaleFactor

        val xTranslation = (size - originalWidth * scaleX) / 2.0f
        val yTranslation = (size - originalHeight * scaleY) / 2.0f

        val transformation = Matrix().apply {
            postTranslate(xTranslation, yTranslation)
            preScale(scaleX, scaleY)
        }

        canvas.drawBitmap(resourceBitmap, transformation, paint)

        drawLines(canvas, size)
        drawAlphaCircle(canvas)

        val xy = (Math.min(mainCanvas.width, mainCanvas.height) / 2) - (size / 2)
        mainCanvas.drawBitmap(output, xy, xy, Paint())
    }

    private fun drawLines(canvas: Canvas, circleDiameter: Float) {
        drawLineInsideCircle(canvas = canvas, segmentHeight = circleDiameter / 2, diameter = circleDiameter)
        drawLineInsideCircle(canvas = canvas, segmentHeight = circleDiameter / 4, diameter = circleDiameter, invert = true)

    }

    private fun drawLineInsideCircle(canvas: Canvas, segmentHeight: Float, diameter: Float, drawX: Boolean = true, drawY: Boolean = true, invert: Boolean = false) {
        val aAngle = Math.acos(1 - (2 * (segmentHeight)).toDouble() / diameter)
        val chordLength = diameter * Math.sin(aAngle)
        val padding = ((width - chordLength) / 2).toFloat()

        val startPosition = chordLength.toFloat() + padding - gradientStrokeWidth

        //draw horizontal line
        if (drawX) canvas.drawLine(padding, segmentHeight, startPosition, segmentHeight, linePaint)

        //draw vertical line
        if (drawY) canvas.drawLine(segmentHeight, padding, segmentHeight, startPosition, linePaint)

        if (invert) {
            //draw invert horizontal line
            if (drawX) canvas.drawLine(padding, segmentHeight + diameter / 2, startPosition, segmentHeight + diameter / 2, linePaint)

            //draw invert vertical line
            if (drawY) canvas.drawLine(segmentHeight + diameter / 2, padding, segmentHeight + diameter / 2, startPosition, linePaint)
        }
    }

    //----------------------------------------------------------------------
    //todo testing pulse animation
    var pulsingCircleScaleFactor: Float = 0f

    fun setPulsingScaleFactor(value: Float) {
        pulsingCircleScaleFactor = value
        invalidate()
    }

    //todo testing pulse animation
    private fun drawAlphaCircle(canvas: Canvas) {
        val newRadius = canvas.width * pulsingCircleScaleFactor

        if (newRadius > (canvas.width / 2 - gradientStrokeWidth)) {
        } else {
            pulsingCirclePaint.alpha = 200 - (255 * pulsingCircleScaleFactor).toInt()
            canvas.drawCircle(canvas.width / 2f, canvas.height / 2f, newRadius, pulsingCirclePaint)
        }
    }

}