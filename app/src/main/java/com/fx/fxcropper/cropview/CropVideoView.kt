package com.fx.fxcropper.cropview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fx.fxcropper.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class CropVideoView : FrameLayout {
    private var mPlayerView: PlayerView? = null
    private var mCropView: CropView? = null
    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var mVideoRotationDegrees: Int = 0
    private var mGuidelines = 1
    private var mFixAspectRatio = false
    private var mAspectRatioX = 1
    private var mAspectRatioY = 1

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CropVideoView, 0, 0)

        try {
            mGuidelines = ta.getInteger(R.styleable.CropVideoView_guidelines, 1)
            mFixAspectRatio = ta.getBoolean(R.styleable.CropVideoView_fixAspectRatio, false)
            mAspectRatioX = ta.getInteger(R.styleable.CropVideoView_aspectRatioX, 1)
            mAspectRatioY = ta.getInteger(R.styleable.CropVideoView_aspectRatioY, 1)
        } finally {
            ta.recycle()
        }

        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.view_crop, this, true)
        mPlayerView = v.findViewById(R.id.playerView)
        mCropView = v.findViewById(R.id.cropView)
        mCropView!!.setInitialAttributeValues(
            mGuidelines,
            mFixAspectRatio,
            mAspectRatioX,
            mAspectRatioY
        )
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        val lp = layoutParams
        if (mVideoRotationDegrees == 90 || mVideoRotationDegrees == 270) {
            if (mVideoWidth >= mVideoHeight) {
                lp.width = (newHeight * (1.0f * mVideoHeight / mVideoWidth)).toInt()
                lp.height = newHeight
            } else {
                lp.width = newWidth
                lp.height = (newWidth * (1.0f * mVideoWidth / mVideoHeight)).toInt()
            }
        } else {
            if (mVideoWidth >= mVideoHeight) {
                lp.width = newWidth
                lp.height = (newWidth * (1.0f * mVideoHeight / mVideoWidth)).toInt()
            } else {
                lp.width = (newHeight * (1.0f * mVideoWidth / mVideoHeight)).toInt()
                lp.height = newHeight
            }
        }

        layoutParams = lp
        val rect = Rect(0, 0, lp.width, lp.height)
        mCropView!!.setBitmapRect(rect)
        mCropView!!.resetCropOverlayView()
    }

    fun setPlayer(player: SimpleExoPlayer) {
        mPlayerView!!.setPlayer(player)
        mCropView!!.resetCropOverlayView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPlayerView!!.setPlayer(null)
    }

    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mCropView!!.setFixedAspectRatio(fixAspectRatio)
    }

    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        mAspectRatioX = aspectRatioX
        mAspectRatioY = aspectRatioY
        mCropView!!.setAspectRatioX(this.mAspectRatioX)
        mCropView!!.setAspectRatioY(this.mAspectRatioY)
    }

    fun initBounds(videoWidth: Int, videoHeight: Int, rotationDegrees: Int) {
        mVideoWidth = videoWidth
        mVideoHeight = videoHeight
        mVideoRotationDegrees = rotationDegrees
    }
}
