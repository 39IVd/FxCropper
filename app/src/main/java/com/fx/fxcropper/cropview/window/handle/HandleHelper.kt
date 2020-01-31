package com.fx.fxcropper.cropview.window.handle

import android.graphics.Rect
import com.fx.fxcropper.cropview.util.AspectRatioUtil
import com.fx.fxcropper.cropview.window.edge.Edge
import com.fx.fxcropper.cropview.window.edge.EdgePair

internal abstract class HandleHelper(
    private val mHorizontalEdge: Edge?,
    private val mVerticalEdge: Edge?
) {
    val activeEdges: EdgePair

    init {
        this.activeEdges = EdgePair(this.mHorizontalEdge, this.mVerticalEdge)
    }

    open fun updateCropWindow(x: Float, y: Float, imageRect: Rect, snapRadius: Float) {
        val activeEdges = this.activeEdges
        val primaryEdge = activeEdges.primary
        val secondaryEdge = activeEdges.secondary
        if (primaryEdge != null) {
            primaryEdge!!.adjustCoordinate(x, y, imageRect, snapRadius, 1.0f)
        }

        if (secondaryEdge != null) {
            secondaryEdge!!.adjustCoordinate(x, y, imageRect, snapRadius, 1.0f)
        }

    }

    abstract fun updateCropWindow(
        var1: Float,
        var2: Float,
        var3: Float,
        var4: Rect,
        var5: Float
    )

    fun getActiveEdges(x: Float, y: Float, targetAspectRatio: Float): EdgePair {
        val potentialAspectRatio = this.getAspectRatio(x, y)
        if (potentialAspectRatio > targetAspectRatio) {
            this.activeEdges.primary = this.mVerticalEdge!!
            this.activeEdges.secondary = this.mHorizontalEdge!!
        } else {
            this.activeEdges.primary = this.mHorizontalEdge!!
            this.activeEdges.secondary = this.mVerticalEdge!!
        }

        return this.activeEdges
    }

    private fun getAspectRatio(x: Float, y: Float): Float {
        val left = if (this.mVerticalEdge === Edge.LEFT) x else Edge.LEFT.get_Coordinate()
        val top = if (this.mHorizontalEdge === Edge.TOP) y else Edge.TOP.get_Coordinate()
        val right = if (this.mVerticalEdge === Edge.RIGHT) x else Edge.RIGHT.get_Coordinate()
        val bottom = if (this.mHorizontalEdge === Edge.BOTTOM) y else Edge.BOTTOM.get_Coordinate()
        return AspectRatioUtil.calculateAspectRatio(left, top, right, bottom)
    }

    companion object {
        private val UNFIXED_ASPECT_RATIO_CONSTANT = 1.0f
    }
}

