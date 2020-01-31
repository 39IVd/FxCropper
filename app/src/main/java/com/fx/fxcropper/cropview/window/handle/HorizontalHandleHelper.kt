package com.fx.fxcropper.cropview.window.handle

import android.graphics.Rect
import com.fx.fxcropper.cropview.util.AspectRatioUtil
import com.fx.fxcropper.cropview.window.edge.Edge

internal class HorizontalHandleHelper(private val mEdge: Edge) : HandleHelper(mEdge, null) {

    override fun updateCropWindow(
        x: Float,
        y: Float,
        targetAspectRatio: Float,
        imageRect: Rect,
        snapRadius: Float
    ) {
        this.mEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio)
        var left = Edge.LEFT.get_Coordinate()
        val top = Edge.TOP.get_Coordinate()
        var right = Edge.RIGHT.get_Coordinate()
        val bottom = Edge.BOTTOM.get_Coordinate()
        val targetWidth = AspectRatioUtil.calculateWidth(top, bottom, targetAspectRatio)
        val currentWidth = right - left
        val difference = targetWidth - currentWidth
        val halfDifference = difference / 2.0f
        left -= halfDifference
        right += halfDifference
        Edge.LEFT.set_Coordinate(left)
        Edge.RIGHT.set_Coordinate(right)
        var offset: Float
        if (Edge.LEFT.isOutsideMargin(
                imageRect,
                snapRadius
            )!! && !this.mEdge.isNewRectangleOutOfBounds(Edge.LEFT, imageRect, targetAspectRatio)
        ) {
            offset = Edge.LEFT.snapToRect(imageRect)
            Edge.RIGHT.offset(-offset)
            this.mEdge.adjustCoordinate(targetAspectRatio)
        }

        if (Edge.RIGHT.isOutsideMargin(
                imageRect,
                snapRadius
            )!! && !this.mEdge.isNewRectangleOutOfBounds(Edge.RIGHT, imageRect, targetAspectRatio)
        ) {
            offset = Edge.RIGHT.snapToRect(imageRect)
            Edge.LEFT.offset(-offset)
            this.mEdge.adjustCoordinate(targetAspectRatio)
        }

    }
}

