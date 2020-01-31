package com.fx.fxcropper.cropview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Pair
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.fx.fxcropper.cropview.util.AspectRatioUtil
import com.fx.fxcropper.cropview.util.HandleUtil
import com.fx.fxcropper.cropview.util.PaintUtil
import com.fx.fxcropper.cropview.window.edge.Edge
import com.fx.fxcropper.cropview.window.handle.Handle

class CropView : View {
    private var mBorderPaint: Paint? = null
    private var mGuidelinePaint: Paint? = null
    private var mCornerPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mBitmapRect: Rect? = null
    private var mHandleRadius: Float = 0.toFloat()
    private var mSnapRadius: Float = 0.toFloat()
    private var mTouchOffset: Pair<Float, Float>? = null
    private var mPressedHandle: Handle? = null
    private var mFixAspectRatio = false
    private var mAspectRatioX = 1
    private var mAspectRatioY = 1
    private var mTargetAspectRatio: Float = 0.toFloat()
    private var mGuidelines: Int = 0
    private var initializedCropWindow: Boolean = false
    private var mCornerExtension: Float = 0.toFloat()
    private var mCornerOffset: Float = 0.toFloat()
    private var mCornerLength: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
        initializedCropWindow = false
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
        initializedCropWindow = false
        init(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initCropWindow(mBitmapRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas, mBitmapRect!!)
        if (showGuidelines()) {
            if (mGuidelines == 2) {
                drawRuleOfThirdsGuidelines(canvas)
            } else if (mGuidelines == 1) {
                if (mPressedHandle != null) {
                    drawRuleOfThirdsGuidelines(canvas)
                }
            } else if (mGuidelines == 0) {
            }
        }

        canvas.drawRect(
            Edge.LEFT.coordinate,
            Edge.TOP.coordinate,
            Edge.RIGHT.coordinate,
            Edge.BOTTOM.coordinate,
            mBorderPaint!!
        )
        drawCorners(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        } else {
            when (event.action) {
                0 -> {
                    onActionDown(event.x, event.y)
                    return true
                }
                1, 3 -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    onActionUp()
                    return true
                }
                2 -> {
                    onActionMove(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
                else -> return false
            }
        }
    }

    fun setBitmapRect(bitmapRect: Rect) {
        mBitmapRect = bitmapRect
        initCropWindow(mBitmapRect)
    }

    fun resetCropOverlayView() {
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect)
            invalidate()
        }

    }


    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mFixAspectRatio = fixAspectRatio
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect)
            invalidate()
        }
    }

    fun setAspectRatioX(aspectRatioX: Int) {
        require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        mAspectRatioX = aspectRatioX
        mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect)
            invalidate()
        }

    }

    fun setAspectRatioY(aspectRatioY: Int) {
        if (aspectRatioY <= 0) {
            throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
        } else {
            mAspectRatioY = aspectRatioY
            mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
            if (initializedCropWindow) {
                initCropWindow(mBitmapRect)
                invalidate()
            }

        }
    }

    fun setInitialAttributeValues(
        guidelines: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int
    ) {
        if (guidelines >= 0 && guidelines <= 2) {
            mGuidelines = guidelines
            mFixAspectRatio = fixAspectRatio
            if (aspectRatioX <= 0) {
                throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
            } else {
                mAspectRatioX = aspectRatioX
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
                if (aspectRatioY <= 0) {
                    throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
                } else {
                    mAspectRatioY = aspectRatioY
                    mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
                }
            }
        } else {
            throw IllegalArgumentException("Guideline value must be set between 0 and 2. See documentation.")
        }
    }

    private fun init(context: Context) {
        val displayMetrics = context.resources.displayMetrics
        mHandleRadius = HandleUtil.getTargetRadius(context)
        mSnapRadius = TypedValue.applyDimension(1, 6.0f, displayMetrics)
        mBorderPaint = PaintUtil.newBorderPaint(context)
        mGuidelinePaint = PaintUtil.newGuidelinePaint()
        mBackgroundPaint = PaintUtil.newBackgroundPaint(context)
        mCornerPaint = PaintUtil.newCornerPaint(context)
        mCornerOffset = TypedValue.applyDimension(1, DEFAULT_CORNER_OFFSET_DP, displayMetrics)
        mCornerExtension = TypedValue.applyDimension(1, DEFAULT_CORNER_EXTENSION_DP, displayMetrics)
        mCornerLength = TypedValue.applyDimension(1, 20.0f, displayMetrics)
        mGuidelines = 1
    }

    private fun initCropWindow(bitmapRect: Rect?) {
        if (!initializedCropWindow) {
            initializedCropWindow = true
        }

        val centerX: Float
        val cropWidth: Float
        if (mFixAspectRatio) {
            val halfCropWidth: Float
            if (AspectRatioUtil.calculateAspectRatio(bitmapRect!!) > mTargetAspectRatio) {
                Edge.TOP.set_Coordinate(bitmapRect!!.top.toFloat())
                Edge.BOTTOM.set_Coordinate(bitmapRect!!.bottom.toFloat())
                centerX = width.toFloat() / 2.0f
                cropWidth = Math.max(
                    40.0f,
                    AspectRatioUtil.calculateWidth(
                        Edge.TOP.get_Coordinate(),
                        Edge.BOTTOM.get_Coordinate(),
                        mTargetAspectRatio
                    )
                )
                if (cropWidth == 40.0f) {
                    mTargetAspectRatio =
                        40.0f / (Edge.BOTTOM.get_Coordinate() - Edge.TOP.get_Coordinate())
                }

                halfCropWidth = cropWidth / 2.0f
                Edge.LEFT.set_Coordinate(centerX - halfCropWidth)
                Edge.RIGHT.set_Coordinate(centerX + halfCropWidth)
            } else {
                Edge.LEFT.set_Coordinate(bitmapRect!!.left.toFloat())
                Edge.RIGHT.set_Coordinate(bitmapRect!!.right.toFloat())
                centerX = height.toFloat() / 2.0f
                cropWidth = Math.max(
                    40.0f,
                    AspectRatioUtil.calculateHeight(
                        Edge.LEFT.get_Coordinate(),
                        Edge.RIGHT.get_Coordinate(),
                        mTargetAspectRatio
                    )
                )
                if (cropWidth == 40.0f) {
                    mTargetAspectRatio =
                        (Edge.RIGHT.get_Coordinate() - Edge.LEFT.get_Coordinate()) / 40.0f
                }

                halfCropWidth = cropWidth / 2.0f
                Edge.TOP.set_Coordinate(centerX - halfCropWidth)
                Edge.BOTTOM.set_Coordinate(centerX + halfCropWidth)
            }
        } else {
            centerX = 0.1f * bitmapRect!!.width().toFloat()
            cropWidth = 0.1f * bitmapRect!!.height().toFloat()
            Edge.LEFT.set_Coordinate(bitmapRect!!.left.toFloat() + centerX)
            Edge.TOP.set_Coordinate(bitmapRect!!.top.toFloat() + cropWidth)
            Edge.RIGHT.set_Coordinate(bitmapRect!!.right.toFloat() - centerX)
            Edge.BOTTOM.set_Coordinate(bitmapRect!!.bottom.toFloat() - cropWidth)
        }

    }

    private fun drawRuleOfThirdsGuidelines(canvas: Canvas) {
        val left = Edge.LEFT.get_Coordinate()
        val top = Edge.TOP.get_Coordinate()
        val right = Edge.RIGHT.get_Coordinate()
        val bottom = Edge.BOTTOM.get_Coordinate()
        val oneThirdCropWidth = Edge.width / 3.0f
        val x1 = left + oneThirdCropWidth
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint!!)
        val x2 = right - oneThirdCropWidth
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint!!)
        val oneThirdCropHeight = Edge.height / 3.0f
        val y1 = top + oneThirdCropHeight
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint!!)
        val y2 = bottom - oneThirdCropHeight
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint!!)
    }

    private fun drawBackground(canvas: Canvas, bitmapRect: Rect) {
        val left = Edge.LEFT.get_Coordinate()
        val top = Edge.TOP.get_Coordinate()
        val right = Edge.RIGHT.get_Coordinate()
        val bottom = Edge.BOTTOM.get_Coordinate()
        canvas.drawRect(
            bitmapRect.left.toFloat(),
            bitmapRect.top.toFloat(),
            bitmapRect.right.toFloat(),
            top,
            mBackgroundPaint!!
        )
        canvas.drawRect(
            bitmapRect.left.toFloat(),
            bottom,
            bitmapRect.right.toFloat(),
            bitmapRect.bottom.toFloat(),
            mBackgroundPaint!!
        )
        canvas.drawRect(bitmapRect.left.toFloat(), top, left, bottom, mBackgroundPaint!!)
        canvas.drawRect(right, top, bitmapRect.right.toFloat(), bottom, mBackgroundPaint!!)
    }

    private fun drawCorners(canvas: Canvas) {
        val left = Edge.LEFT.get_Coordinate()
        val top = Edge.TOP.get_Coordinate()
        val right = Edge.RIGHT.get_Coordinate()
        val bottom = Edge.BOTTOM.get_Coordinate()
        canvas.drawLine(
            left - mCornerOffset,
            top - mCornerExtension,
            left - mCornerOffset,
            top + mCornerLength,
            mCornerPaint!!
        )
        canvas.drawLine(
            left,
            top - mCornerOffset,
            left + mCornerLength,
            top - mCornerOffset,
            mCornerPaint!!
        )
        canvas.drawLine(
            right + mCornerOffset,
            top - mCornerExtension,
            right + mCornerOffset,
            top + mCornerLength,
            mCornerPaint!!
        )
        canvas.drawLine(
            right,
            top - mCornerOffset,
            right - mCornerLength,
            top - mCornerOffset,
            mCornerPaint!!
        )
        canvas.drawLine(
            left - mCornerOffset,
            bottom + mCornerExtension,
            left - mCornerOffset,
            bottom - mCornerLength,
            mCornerPaint!!
        )
        canvas.drawLine(
            left,
            bottom + mCornerOffset,
            left + mCornerLength,
            bottom + mCornerOffset,
            mCornerPaint!!
        )
        canvas.drawLine(
            right + mCornerOffset,
            bottom + mCornerExtension,
            right + mCornerOffset,
            bottom - mCornerLength,
            mCornerPaint!!
        )
        canvas.drawLine(
            right,
            bottom + mCornerOffset,
            right - mCornerLength,
            bottom + mCornerOffset,
            mCornerPaint!!
        )
    }

    private fun onActionDown(x: Float, y: Float) {
        val left = Edge.LEFT.get_Coordinate()
        val top = Edge.TOP.get_Coordinate()
        val right = Edge.RIGHT.get_Coordinate()
        val bottom = Edge.BOTTOM.get_Coordinate()
        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right, bottom, mHandleRadius)
        if (mPressedHandle != null) {
            mTouchOffset = HandleUtil.getOffset(mPressedHandle, x, y, left, top, right, bottom)
            invalidate()
        }
    }

    private fun onActionUp() {
        if (mPressedHandle != null) {
            mPressedHandle = null
            invalidate()
        }
    }

    private fun onActionMove(x: Float, y: Float) {
        var x = x
        var y = y
        if (mPressedHandle != null) {
            x += mTouchOffset!!.first
            y += mTouchOffset!!.second
            if (mFixAspectRatio) {
                mPressedHandle!!.updateCropWindow(
                    x,
                    y,
                    mTargetAspectRatio,
                    mBitmapRect!!,
                    mSnapRadius
                )
            } else {
                mPressedHandle!!.updateCropWindow(x, y, mBitmapRect!!, mSnapRadius)
            }
            invalidate()
        }
    }

    companion object {
        private val DEFAULT_CORNER_THICKNESS_DP = PaintUtil.cornerThickness
        private val DEFAULT_LINE_THICKNESS_DP = PaintUtil.lineThickness
        private val DEFAULT_CORNER_OFFSET_DP: Float
        private val DEFAULT_CORNER_EXTENSION_DP: Float

        fun showGuidelines(): Boolean {
            return Math.abs(Edge.LEFT.get_Coordinate() - Edge.RIGHT.get_Coordinate()) >= 100.0f && Math.abs(
                Edge.TOP.get_Coordinate() - Edge.BOTTOM.get_Coordinate()
            ) >= 100.0f
        }

        init {
            DEFAULT_CORNER_OFFSET_DP =
                DEFAULT_CORNER_THICKNESS_DP / 2.0f - DEFAULT_LINE_THICKNESS_DP / 2.0f
            DEFAULT_CORNER_EXTENSION_DP =
                DEFAULT_CORNER_THICKNESS_DP / 2.0f + DEFAULT_CORNER_OFFSET_DP
        }
    }
}
