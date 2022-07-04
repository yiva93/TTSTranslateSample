package com.ttstranslate.global.ui.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.ttstranslate.R

class RipplePulseLayout : RelativeLayout {
    private lateinit var mPaint: Paint
    private lateinit var mAnimatorSet: AnimatorSet
    private var mIsAnimating = false
    private lateinit var mRippleView: RippleView

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    /**
     * Initializes the required ripple views
     * @param context
     * @param attrs
     */
    private fun init(context: Context, attrs: AttributeSet) {
        if (isInEditMode) {
            return
        }
        //reading the attributes
        val attrValues: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RipplePulseLayout)
        val color: Int = attrValues.getColor(
            R.styleable.RipplePulseLayout_rippleColor,
            ContextCompat.getColor(context, R.color.appPrimaryA50)
        )
        val startRadius: Float = attrValues.getDimension(
            R.styleable.RipplePulseLayout_startRadius,
            measuredWidth.toFloat()
        )
        val endRadius: Float = attrValues.getDimension(
            R.styleable.RipplePulseLayout_endRadius,
            (measuredWidth * 2).toFloat()
        )
        val strokeWidth: Float =
            attrValues.getDimension(R.styleable.RipplePulseLayout_strokeWidth, 4f)
        val duration: Int =
            attrValues.getInteger(R.styleable.RipplePulseLayout_duration, DEFAULT_DURATION)
        var rippleType: String? = attrValues.getString(R.styleable.RipplePulseLayout_rippleType)
        if (rippleType.isNullOrEmpty()) {
            rippleType = RIPPLE_TYPE_FILL
        }
        //initialize stuff
        initializePaint(color, rippleType, strokeWidth)
        initializeRippleView(endRadius, startRadius, strokeWidth)
        initializeAnimators(startRadius, endRadius, duration)
        attrValues.recycle()
    }

    private fun initializeRippleView(endRadius: Float, startRadius: Float, strokeWidth: Float) {
        mRippleView = RippleView(context, mPaint, startRadius)
        val params = LayoutParams(
            2 * (endRadius + strokeWidth).toInt(),
            2 * (endRadius + strokeWidth).toInt()
        )
        params.addRule(CENTER_IN_PARENT, TRUE)
        addView(mRippleView, params)
        mRippleView.visibility = View.INVISIBLE
    }

    @Keep
    private fun initializeAnimators(startRadius: Float, endRadius: Float, duration: Int) {
        mAnimatorSet = AnimatorSet()
        val scaleXAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(mRippleView, "radius", startRadius, endRadius)
        scaleXAnimator.repeatCount = ValueAnimator.INFINITE
        val alphaAnimator: ObjectAnimator = ObjectAnimator.ofFloat(mRippleView, "alpha", 1f, 0f)
        alphaAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimatorSet.duration = duration.toLong()
        mAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        mAnimatorSet.playTogether(scaleXAnimator, alphaAnimator)
    }

    /**
     * Starts the ripple animation
     */
    fun startRippleAnimation() {
        if (mIsAnimating) {
            //already animating
            return
        }
        mRippleView.visibility = View.VISIBLE
        mAnimatorSet.start()
        mIsAnimating = true
    }

    /**
     * Stops the ripple animation
     */
    fun stopRippleAnimation() {
        if (!mIsAnimating) {
            //already not animating
            return
        }
        mAnimatorSet.end()
        mRippleView.visibility = View.INVISIBLE
        mIsAnimating = false
    }

    private fun initializePaint(color: Int, rippleType: String, strokeWidth: Float) {
        mPaint = Paint()
        mPaint.color = color
        mPaint.isAntiAlias = true
        if (rippleType == RIPPLE_TYPE_STROKE) {
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
        } else {
            mPaint.style = Paint.Style.FILL
            mPaint.strokeWidth = 0f
        }
    }

    companion object {
        const val DEFAULT_DURATION = 2000
        const val RIPPLE_TYPE_FILL = "0"
        const val RIPPLE_TYPE_STROKE = "1"
        const val TAG = "RipplePulseLayout"
    }
}

@Keep
@SuppressLint("ViewConstructor")
private class RippleView(context: Context, private val mPaint: Paint, private var mRadius: Float) :
    View(context) {
    var radius: Float
        get() = mRadius
        set(radius) {
            mRadius = radius
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mRadius, mPaint)
    }
}