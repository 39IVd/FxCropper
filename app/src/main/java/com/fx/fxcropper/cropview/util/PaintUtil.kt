package com.fx.fxcropper.cropview.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue

object PaintUtil {
    val cornerThickness: Float
        get() = 5.0f

    val lineThickness: Float
        get() = 3.0f

    fun newBorderPaint(context: Context): Paint {
        val borderPaint = Paint()
        borderPaint.style = Paint.Style.STROKE
        return borderPaint
    }

    fun newGuidelinePaint(): Paint {
        val paint = Paint()
        paint.color = Color.parseColor("#AAFFFFFF")
        paint.strokeWidth = 1.0f
        return paint
    }

    fun newBackgroundPaint(context: Context): Paint {
        val paint = Paint()
        paint.color = Color.parseColor("#B0000000")
        return paint
    }

    fun newCornerPaint(context: Context): Paint {
        val lineThicknessPx = TypedValue.applyDimension(1, 5.0f, context.resources.displayMetrics)
        val cornerPaint = Paint()
        cornerPaint.color = Color.parseColor("#37efba")
        cornerPaint.strokeWidth = lineThicknessPx
        cornerPaint.style = Paint.Style.STROKE
        return cornerPaint
    }
}
