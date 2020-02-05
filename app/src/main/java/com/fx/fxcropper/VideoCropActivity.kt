package com.fx.fxcropper

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fx.fxcropper.cropview.CropVideoView
import com.fx.fxcropper.player.VideoPlayer
import java.io.File
import java.util.*

class VideoCropActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var ic_play: ImageView
    lateinit var ic_rewind: ImageView
    lateinit var text_video_name: TextView
    lateinit var mVideoPlayer: VideoPlayer
    lateinit var mCropVideoView: CropVideoView
    lateinit var srcFile: String
    lateinit var dstFile: String
    lateinit var layout_play : RelativeLayout
    lateinit var layout_rewind : RelativeLayout
    var list_preset = mutableListOf<RelativeLayout>()

    var selected_preset = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_crop)
        componentSetting()

        if (intent.extras != null) {
            srcFile = intent.extras.getString("EXTRA_PATH")
        }

        var videoname = srcFile.substring(srcFile.lastIndexOf('/') + 1)
        text_video_name?.setText(videoname)
        Log.d("initPlayer uri ", srcFile)

        dstFile =
            (Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + Date().time
                    + "mp4")

        initPlayer(srcFile)

    }

    fun componentSetting() {

        ic_play = findViewById(R.id.ic_play) as ImageView
        ic_rewind = findViewById(R.id.ic_rewind) as ImageView
        text_video_name = findViewById(R.id.text_video_name) as TextView
        layout_play = findViewById(R.id.layout_play) as RelativeLayout
        layout_rewind = findViewById(R.id.layout_rewind) as RelativeLayout
        for (i in 1..6) {
            var preset_id =
                resources.getIdentifier("preset_$i", "id", "com.fx.fxcropper")
            list_preset.add(findViewById(preset_id) as RelativeLayout)
        }

        var preset_1 = findViewById(R.id.preset_1) as RelativeLayout
        preset_1.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)

        mCropVideoView = findViewById(R.id.cropVideoView) as CropVideoView

    }

    private fun initPlayer(uri: String) {
        Log.d("initPlayer uri ", uri)
        if (!File(uri).exists()) {
            Toast.makeText(this, "File doesn't exists", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        mVideoPlayer = VideoPlayer(this)
        mCropVideoView.setPlayer(mVideoPlayer!!.player)
        mVideoPlayer.initMediaSource(this, uri)
        fetchVideoInfo(uri)
    }

    private fun fetchVideoInfo(uri: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(File(uri).absolutePath)
        val videoWidth =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        val videoHeight =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        val rotationDegrees =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))

        mCropVideoView.initBounds(videoWidth, videoHeight, rotationDegrees)
    }

    override fun onClick(v: View) {
        if(v.id==R.id.layout_play) {
            if (mVideoPlayer.isPlaying) {
                mVideoPlayer.play(false)
                ic_play.setBackgroundResource(R.drawable.ic_white_play)
            } else {
                mVideoPlayer.play(true)
                ic_play.setBackgroundResource(R.drawable.ic_white_pause)
            }
        }
        else if(v.id==R.id.layout_rewind) {
            mVideoPlayer.seekTo(0)
            mVideoPlayer.play(true)
            ic_play.setBackgroundResource(R.drawable.ic_white_pause)
        }
        else {
            when(v.id) {
                R.id.preset_1 -> {
                    mCropVideoView.setFixedAspectRatio(false)
                    selected_preset = 1
                }
                R.id.preset_2 -> {
                    mCropVideoView.setFixedAspectRatio(true)
                    mCropVideoView.setAspectRatio(10, 10)
                    selected_preset = 2
                }
                R.id.preset_3 -> {
                    mCropVideoView.setFixedAspectRatio(true)
                    mCropVideoView.setAspectRatio(4, 5)
                    selected_preset = 3
                }
                R.id.preset_4 -> {
                    mCropVideoView.setFixedAspectRatio(true)
                    mCropVideoView.setAspectRatio(16, 9)
                    selected_preset = 4
                }
                R.id.preset_5 -> {
                    mCropVideoView.setFixedAspectRatio(true)
                    mCropVideoView.setAspectRatio(9, 16)
                    selected_preset = 5
                }
                R.id.preset_6 -> {
                    mCropVideoView.setFixedAspectRatio(true)
                    mCropVideoView.setAspectRatio(16, 9)
                    selected_preset = 6
                }
            }
            for(i in 1..list_preset.size) {
                if(i!=selected_preset) {
                    list_preset[i-1].setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                }
                else {
                    list_preset[i-1].setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                }
            }
        }

    }
}
