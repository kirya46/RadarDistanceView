package com.common.radardistanceview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * Created by Kirill Stoianov on 05/09/18.
 *
 * Radar view with scaling background [Bitmap].
 *
 * For scale background image use [RadarDistanceView.scaleFactor] with range 0 -> 50.
 *
 * For animate pulsing circles use [setPulsingPrimaryScaleFactor] and [setPulsingSecondaryScaleFactor]
 * in combination with [ValueAnimator] or [startPulsingPrimaryAnim] and [startPulsingSecondaryAnim].
 *
 * IMPORTANT NOTE: this view override [onMeasure] to make [RadarDistanceView] as square.
 */
class RadarDistanceView(context: Context, attributeSet: AttributeSet?, def: Int) : View(context, attributeSet, def) {

    companion object {
        val TAG: String = RadarDistanceView::class.java.simpleName
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    /**
     * Scale factor of [resourceBitmap].
     *
     * Use range 0f -> .5f.
     */
    private var scaleFactor = 0.0f

    /**
     * Scale factor for primary pulsing circle.
     * Use range 0f -> 1f.
     */
    private var pulsingPrimaryScaleFactor: Float = 0f

    /**
     * Scale factor for secondary pulsing circle.
     * Use range 0f -> 1f.
     */
    private var pulsingSecondaryScaleFactor: Float = 0f

    /**
     * Radar gradient stroke width.
     */
    private val gradientStrokeWidth by lazy {
        15 / resources.displayMetrics.density
    }

    private val gridLineStrokeWidth by lazy {
        4 / resources.displayMetrics.density
    }

    /**
     * Source bitmap which must be scaled.
     */
    private val resourceBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.img_kasha_radar)
    }

    /**
     * Paint for radar gradient stroke.
     */
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

    /**
     * Paint for radar grid lines.
     */
    private val linePaint by lazy {
        Paint().apply {
            val lineColor = Color.parseColor("#45ffffff")
            color = lineColor
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = gridLineStrokeWidth
        }
    }

    /**
     * Paint for pulsing circles.
     */
    private val pulsingCirclePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#00ffffff")
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 4f
        }
    }

    /**
     * Paint for inner circle.
     */
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        //Recycle bitmap when view detach from window
        resourceBitmap.recycle()
    }

    /**
     * Set scale factor for [resourceBitmap].
     */
    fun setScaleFactor(value: Float) {
        scaleFactor = value
        invalidate()
    }

    /**
     * Set scale factor for primary pulsing circle.
     * Use range 0f -> 1f
     */
    fun setPulsingPrimaryScaleFactor(value: Float) {
        pulsingPrimaryScaleFactor = value
        invalidate()
    }

    /**
     * Set scale factor for secondary pulsing circle.
     * Use range 0f -> 1f
     */
    fun setPulsingSecondaryScaleFactor(value: Float) {
        pulsingSecondaryScaleFactor = value
        invalidate()
    }


    /**
     * Draw radar circle gradient stroke.
     */
    private fun drawGradientStroke(canvas: Canvas) {
        val width: Float = canvas.width.toFloat()
        val height: Float = canvas.height.toFloat()
        val radius = Math.min(width, height) / 2
        canvas.drawCircle(width / 2, height / 2, radius, mainGradientStrokePaint)
        canvas.drawCircle(width / 2, height / 2, radius - gradientStrokeWidth, transparentCirclePaint)
    }

    /**
     * Draw radar scaled image.
     */
    private fun drawImage(mainCanvas: Canvas) {

        val size = (Math.min(mainCanvas.width, mainCanvas.height) - gradientStrokeWidth)

        val output = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)

        val color = Color.CYAN
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        canvas.drawCircle(size / 2f, size / 2f, (size - gradientStrokeWidth) / 2f, paint)

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
        drawPulsingCircle(canvas)

        val xy = (Math.min(mainCanvas.width, mainCanvas.height) / 2) - (size / 2)
        mainCanvas.drawBitmap(output, xy, xy, Paint())
    }

    /**
     * Draw radar grid lines in circle.
     */
    private fun drawLines(canvas: Canvas, circleDiameter: Float) {
        drawLineInsideCircle(canvas = canvas, segmentHeight = circleDiameter / 2, diameter = circleDiameter)
        drawLineInsideCircle(canvas = canvas, segmentHeight = circleDiameter / 4, diameter = circleDiameter, invert = true)
    }

    /**
     * Draw radar grid lines.
     *
     * @param canvas - destination canvas
     * @param segmentHeight - line distance from x0 y0 coordinates of circle
     * @param diameter - circle diameter
     * @param drawX - is need draw line on x-asix
     * @param drawY - is need draw line on y-asix
     * @param invert - is need draw destination line on mirror circle sector.
     */
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

    /**
     * Draw pulsing circles.
     *
     * @see startPulsingPrimaryAnim
     * @see startPulsingSecondaryAnim
     */
    private fun drawPulsingCircle(canvas: Canvas) {

        //draw first circle
        var scaledRadius = (canvas.height * pulsingPrimaryScaleFactor) / 2 //scale circle radius
        var scaledAlpha = 70 - (pulsingPrimaryScaleFactor * 70).toInt() //decrease alpha from 1 -> 0
        pulsingCirclePaint.alpha = if (scaledAlpha <= 0) 0 else scaledAlpha

        canvas.drawCircle(canvas.width / 2f, canvas.height / 2f, scaledRadius, pulsingCirclePaint)


        //draw second circle
        scaledRadius = (canvas.height * pulsingSecondaryScaleFactor) / 2 //scale circle radius
        scaledAlpha = 70 - (pulsingSecondaryScaleFactor * 70).toInt() //decrease alpha from 1 -> 0
        pulsingCirclePaint.alpha = if (scaledAlpha <= 0) 0 else scaledAlpha

        canvas.drawCircle(canvas.width / 2f, canvas.height / 2f, scaledRadius, pulsingCirclePaint)
    }

    /**
     * Animate primary pulsing circle.
     * By default use range 0f -> 1f
     */
    fun startPulsingPrimaryAnim(from: Float = 0f, to: Float = 1f) {
        ValueAnimator.ofFloat(from, to).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                this@RadarDistanceView.setPulsingPrimaryScaleFactor(animatedValue)
            }
            duration = 5000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    /**
     * Animate secondary pulsing circle.
     * By default use range 0f -> 1f
     */
    fun startPulsingSecondaryAnim(from: Float = 0f, to: Float = 1f, startDelay: Long = 2500) {
        ValueAnimator.ofFloat(from, to).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                this@RadarDistanceView.setPulsingSecondaryScaleFactor(animatedValue)
            }
            this.startDelay = startDelay
            duration = 5000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

}