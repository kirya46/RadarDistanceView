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

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    companion object {
        val TAG: String = RadarDistanceView::class.java.simpleName
    }

    private var angle = 0.0f

    fun setAngle(value: Float) {
        angle = value
        invalidate()
    }

    val resource: Bitmap by lazy {
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
    private val gradientStrokeWidth = 15F

    private val transparentCircle by lazy {
        Paint().apply {
            color = Color.TRANSPARENT
            isAntiAlias = true
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = canvas.width.toFloat()
        val height: Float = canvas.height.toFloat()

        drawGradientStroke(width, height, canvas)
        drawImage(canvas)

    }

    private fun drawLines(width: Float, height: Float, canvas: Canvas) {
        canvas.drawLine(width / 3, 0f, width / 3, height, linePaint)
        canvas.drawLine(0f, height / 3, width, height / 3, linePaint)
        canvas.drawLine(width / 1.5f, 0f, width / 1.5f, height, linePaint)
        canvas.drawLine(0f, height / 1.5f, width, height / 1.5f, linePaint)
    }

    private fun drawImage(mainCanvas: Canvas) {
        val size = (Math.min(mainCanvas.width, mainCanvas.height) - gradientStrokeWidth)

        val output = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)

        val color = Color.CYAN
        val paint = Paint()
        val rect = Rect(0, 0, size.toInt(), size.toInt())

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        canvas.drawCircle(size / 2f, size / 2f,
                size / 2f, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val originalWidth = resource.width
        val originalHeight = resource.height

        val scale = (size / originalWidth) + angle

        val xTranslation = 0.0f
        val yTranslation = (size - originalHeight * scale) / 2.0f

        val transformation = Matrix().apply {
            postTranslate(xTranslation, yTranslation)
            preScale(scale, scale)
        }

        canvas.drawBitmap(resource, transformation, paint)

        drawLines(size, size, canvas)

        val xy = (Math.min(mainCanvas.width, mainCanvas.height) / 2) - (size / 2)
        mainCanvas.drawBitmap(output, xy, xy, Paint())
    }

    private fun drawGradientStroke(width: Float, height: Float, canvas: Canvas) {
        val radius = Math.min(width, height) / 2
        canvas.drawCircle(width / 2, height / 2, radius, mainGradientStrokePaint)
        canvas.drawCircle(width / 2, height / 2, radius - gradientStrokeWidth, transparentCircle)
    }
}