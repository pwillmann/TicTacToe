package com.pwillmann.development.udemylearning

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.util.DisplayMetrics
import android.util.Log
import android.animation.ValueAnimator
import android.graphics.*
import android.support.annotation.ColorRes
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator


/**
 * Created by Patrick on 10.06.2017.
 */
class WinnerOverlay : View {
    val TAG: String = WinnerOverlay::class.java.simpleName

    var paint = Paint()
    var transparentPaint = Paint()

    val lineWidth: Float = DpToPx(5)
    var lineColor: Int = Color.GREEN

    var mStartX: Float = -1F
    var mStartY: Float = -1F
    var mEndX: Float = -1F
    var mEndY: Float = -1F

    
    init {
        lineColor = ContextCompat.getColor(context, R.color.grey_light)
        paint.color = lineColor
        paint.strokeWidth = lineWidth
        transparentPaint.color = Color.TRANSPARENT
        transparentPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    constructor(context: Context?): this(context, null)
    constructor(context: Context?,attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context?,attrs: AttributeSet?, defStyle: Int): this(context, attrs, defStyle, 0)
    constructor(context: Context?,attrs: AttributeSet?, defStyle: Int, defStyleRes: Int): super(context, attrs, defStyle, defStyleRes)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        this.setMeasuredDimension(parentWidth, parentHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mStartX > 0 && mStartY > 0 && mEndX > 0 && mEndY > 0) {
            canvas?.drawLine(mStartX, mStartY, mEndX, mEndY, paint)
        } else {
            canvas?.drawRect(0F,0F, this.measuredWidth.toFloat(), this.measuredHeight.toFloat(), transparentPaint)
            canvas?.drawRect(0F,0F, 0F, 0F, transparentPaint)
        }
    }

    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, @ColorRes lineColor: Int) {
        this.mStartX = startX
        this.mStartY = startY
        this.mEndX = endX
        this.mEndY = endY
        this.lineColor = ContextCompat.getColor(context, lineColor)
        paint.color = this.lineColor
        Log.d(TAG, "drawLine() called with StartX:[$startX], StartY:[$startY] EndX:[$endX], EndY:[$endY]")


        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 600
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            run {
                val runPercentage = animation.animatedFraction
                this.mEndX = startX + ((endX - startX) * runPercentage)
                this.mEndY = startY + ((endY - startY) * runPercentage)
                Log.d(TAG, "drawLine() called with StartX:[$mStartX], StartY:[$mStartY] EndX:[$mEndX], EndY:[$mEndY]")
                this.invalidate()
            }
        }
        animator.start()
    }

    fun clear(){
        this.mStartX = -1F
        this.mStartY = -1F
        this.mEndX = -1F
        this.mEndY = -1F

        this.invalidate()
    }


    fun DpToPx(dp: Int): Float {
        val metrics = Resources.getSystem().getDisplayMetrics()
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


}