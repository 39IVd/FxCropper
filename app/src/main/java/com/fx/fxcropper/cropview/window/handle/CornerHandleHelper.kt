package com.fx.fxcropper.cropview.window.handle

import android.graphics.Rect
import com.fx.fxcropper.cropview.window.edge.Edge

internal class CornerHandleHelper(horizontalEdge: Edge, verticalEdge: Edge) :
    HandleHelper(horizontalEdge, verticalEdge) {

    override fun updateCropWindow(
        x: Float,
        y: Float,
        targetAspectRatio: Float,
        imageRect: Rect,
        snapRadius: Float
    ) {
        val activeEdges = this.getActiveEdges(x, y, targetAspectRatio)
        val primaryEdge = activeEdges.primary
        val secondaryEdge = activeEdges.secondary
        primaryEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio)
        secondaryEdge.adjustCoordinate(targetAspectRatio)
        if (secondaryEdge.isOutsideMargin(imageRect, snapRadius)) {
            secondaryEdge.snapToRect(imageRect)
            primaryEdge.adjustCoordinate(targetAspectRatio)
        }

    }
}