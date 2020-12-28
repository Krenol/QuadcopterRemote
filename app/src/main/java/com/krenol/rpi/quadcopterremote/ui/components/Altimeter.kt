package com.krenol.rpi.quadcopterremote.ui.components

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.databinding.BindingAdapter
import com.krenol.rpi.quadcopterremote.R
import kotlin.math.round

class Altimeter @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mAltitude: Double = 0.0
    private var mStepLarge: Int = 10
    private var mStepSmall: Int = 1
    private val mBitmapPaint = Paint().apply {
        isFilterBitmap = false
    }

    private val mBitmap: Bitmap by lazy {
        val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.TRANSPARENT
        canvas.drawRect(RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat()), paint)
        bitmap
    }

    private val mSrcCanvas: Canvas by lazy {
        Canvas(mBitmap)
    }

    private lateinit var mIndicatorText: Paint
    private lateinit var mAltimeterText: Paint
    private lateinit var mAltimeter: Paint
    private lateinit var mAltimeterLines: Paint
    private lateinit var mIndicatorFrame: Paint



    init {
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.Altimeter, 0, 0
            )
            mAltimeter= Paint().apply {
                color = typedArray.getColor(R.styleable.Altimeter_altimeterBackground, Color.DKGRAY)
            }
            mStepSmall = typedArray.getInteger(R.styleable.Altimeter_stepSize, 1)
            mStepLarge = typedArray.getInteger(R.styleable.Altimeter_stepSizeLarge, 5)

            mIndicatorFrame = Paint().apply {
                color = typedArray.getColor(R.styleable.Altimeter_indicatorColor, Color.BLACK)
            }
            mIndicatorText = Paint().apply {
                color = typedArray.getColor(R.styleable.Altimeter_indicatorTextColor, Color.WHITE)
                textSize = typedArray.getDimension(R.styleable.Altimeter_indicatorTextSize, 50f)
                textAlign = Paint.Align.LEFT
            }
            mAltimeterText = Paint().apply {
                color = typedArray.getColor(R.styleable.Altimeter_altimeterTextColor, Color.WHITE)
                textSize = typedArray.getDimension(R.styleable.Altimeter_altimeterTextSize, 45f)
                textAlign = Paint.Align.LEFT
            }
            mAltimeterLines = Paint().apply {
                color = typedArray.getColor(R.styleable.Altimeter_altimeterLineColor, Color.WHITE)
            }
            typedArray.recycle()
        }
    }


    fun setAltitude(altitude: Double) {
        mAltitude = round(altitude * 10) / 10
        invalidate()
    }

    fun getAltitude() : Double {
        return mAltitude
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        val sc = saveLayer(canvas)
        //canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.drawBitmap(drawAltimeter(), 0f, 0f, mBitmapPaint)
        canvas.restoreToCount(sc)
    }

    private fun drawAltimeter() : Bitmap {
        val canvas = mSrcCanvas
        val width = mWidth.toFloat()
        val height = mHeight.toFloat()
        val centerY = height / 2
        val offset = 0.13f
        val path = Path()
        val pxH = height / (6 * mStepSmall)
        val off = mAltitude % mStepSmall
        val w = 5f
        var pos: Float
        var h: Float
        //draw altimeter
        canvas.drawRect(RectF(0f, 0f, mWidth.toFloat() * 0.8f, mHeight.toFloat()), mAltimeter)
        for(i in -3..3){
            pos = (centerY + (off + mStepSmall * i) * pxH).toFloat()
            h = (mAltitude - off - mStepSmall * i).toFloat()
            canvas.drawRect(0f, pos - w, 0.1f * width, pos + w, mAltimeterLines)
            canvas.drawText(
                "$h m",
                width * 0.15f,
                pos + mAltimeterText.textSize / 3,
                mAltimeterText
            )
        }

        //draw height indicator
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(width * 0.25f, centerY * (1 + offset / 3))
        path.lineTo(width * 0.18f, centerY)
        path.lineTo(width * 0.25f, centerY * (1 - offset / 3))
        path.close()
        canvas.drawRect(
            width * 0.25f,
            centerY * (1 + offset),
            width,
            centerY * (1 - offset),
            mIndicatorFrame
        )
        canvas.drawPath(path, mIndicatorFrame)
        //add height text
        canvas.drawText(
            "$mAltitude m",
            width * 0.325f,
            centerY + mIndicatorText.textSize / 3,
            mIndicatorText
        )
        // add indicator lines

        return mBitmap
    }

    private fun saveLayer(canvas: Canvas): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return canvas.saveLayer(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), null)
        } else {
            @Suppress("DEPRECATION")
            return canvas.saveLayer(
                0f,
                0f,
                mWidth.toFloat(),
                mHeight.toFloat(),
                null,
                Canvas.ALL_SAVE_FLAG
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }
}