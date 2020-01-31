package com.fx.fxcropper.cropview.window.handle

import android.graphics.Rect
import com.fx.fxcropper.cropview.util.AspectRatioUtil
import com.fx.fxcropper.cropview.window.edge.Edge

internal class VerticalHandleHelper(private val mEdge: Edge) : HandleHelper(null, mEdge) {

    override fun updateCropWindow(
        x: Float,
        y: Float,
        targetAspectRatio: Float,
        imageRect: Rect,
        snapRadius: Float
    ) {
        this.mEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio)
        val left = Edge.LEFT.get_Coordinate()
        var top = Edge.TOP.get_Coordinate()
        val right = Edge.RIGHT.get_Coordinate()
        var bottom = Edge.BOTTOM.get_Coordinate()
        val targetHeight = AspectRatioUtil.calculateHeight(left, right, targetAspectRatio)
        val currentHeight = bottom - top
        val difference = targetHeight - currentHeight
        val halfDifference = difference / 2.0f
        top -= halfDifference
        bottom += halfDifference
        Edge.TOP.set_Coordinate(top)
        Edge.BOTTOM.set_Coordinate(bottom)
        var offset: Float
        if (Edge.TOP.isOutsideMargin(
                imageRect,
                snapRadius
            ) && !this.mEdge.isNewRectangleOutOfBounds(Edge.TOP, imageRect, targetAspectRatio)
        ) {
            offset = Edge.TOP.snapToRect(imageRect)
            Edge.BOTTOM.offset(-offset)
            this.mEdge.adjustCoordinate(targetAspectRatio)
        }

        if (Edge.BOTTOM.isOutsideMargin(
                imageRect,
                snapRadius
            ) && !this.mEdge.isNewRectangleOutOfBounds(Edge.BOTTOM, imageRect, targetAspectRatio)
        ) {
            offset = Edge.BOTTOM.snapToRect(imageRect)
            Edge.TOP.offset(-offset)
            this.mEdge.adjustCoordinate(targetAspectRatio)
        }

    }
}

