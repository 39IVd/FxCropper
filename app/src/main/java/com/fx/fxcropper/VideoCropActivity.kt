package com.fx.fxcropper

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fx.fxcropper.cropview.CropVideoView
import com.fx.fxcropper.player.VideoPlayer
import java.io.File
import java.util.*

class VideoCropActivity : AppCompatActivity(), View.OnClickListener {
    private var ic_play: ImageButton? = null
    private var ic_rewind : ImageButton? = null
    private var text_video_name: TextView? = null
    private var preset_default : RelativeLayout? = null
    private var preset_insta_1_1 : RelativeLayout? = null
    private var preset_insta_4_5 : RelativeLayout? = null
    private var preset_youtube_16_9 : RelativeLayout? = null
    private var preset_youtube_9_16 : RelativeLayout? = null
    private var preset_tiktok_16_9 : RelativeLayout? = null
    private var mVideoPlayer: VideoPlayer? = null
    private var mCropVideoView: CropVideoView? = null

    var srcFile: String? = null
    var dstFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_crop2)
        componentSetting()

        if (intent.extras != null) {
            srcFile = intent.extras.getString("EXTRA_PATH")
        }

        var videoname = srcFile!!.substring(srcFile!!.lastIndexOf('/') + 1)
        text_video_name?.setText(videoname)
        Log.d("initPlayer uri ", srcFile.toString())

        dstFile =
            (Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + Date().time
                    + "mp4")

        initPlayer(srcFile.toString())



    }

    fun componentSetting() {

        ic_play = findViewById(R.id.ic_play) as ImageButton
        ic_rewind = findViewById(R.id.ic_rewind) as ImageButton
        text_video_name = findViewById(R.id.text_video_name) as TextView
        preset_default = findViewById(R.id.preset_default) as RelativeLayout
        preset_insta_1_1 = findViewById(R.id.preset_insta_1_1) as RelativeLayout
        preset_insta_4_5 = findViewById(R.id.preset_insta_4_5) as RelativeLayout
        preset_youtube_16_9 = findViewById(R.id.preset_youtube_16_9) as RelativeLayout
        preset_youtube_9_16 = findViewById(R.id.preset_youtube_9_16) as RelativeLayout
        preset_tiktok_16_9 = findViewById(R.id.preset_tiktok_16_9) as RelativeLayout
        mCropVideoView = findViewById(R.id.cropVideoView) as CropVideoView
    }
    private fun initPlayer(uri : String) {
        Log.d("initPlayer uri ", uri)
        if (!File(uri).exists()) {
            Toast.makeText(this, "File doesn't exists", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        mVideoPlayer = VideoPlayer(this)
        mCropVideoView?.setPlayer(mVideoPlayer!!.player)
        mVideoPlayer?.initMediaSource(this, uri)
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

        mCropVideoView?.initBounds(videoWidth, videoHeight, rotationDegrees)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ic_play-> {
                if (mVideoPlayer!!.isPlaying) {
                    mVideoPlayer?.play(false)
                    ic_play!!.setBackgroundResource(R.drawable.ic_white_play)
                }
                else {
                    mVideoPlayer?.play(true)
                    ic_play!!.setBackgroundResource(R.drawable.ic_white_pause)
                }
            }
            R.id.ic_rewind-> {
                mVideoPlayer?.seekTo(0)
                mVideoPlayer?.play(true)
//                mVideoView!!.seekTo(0)
                ic_play!!.setBackgroundResource(R.drawable.ic_white_pause)

            }
            R.id.preset_default-> {
                mCropVideoView?.setFixedAspectRatio(false)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_insta_1_1-> {
                mCropVideoView?.setFixedAspectRatio(true)
                mCropVideoView?.setAspectRatio(10, 10)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_insta_4_5-> {
                mCropVideoView?.setFixedAspectRatio(true)
                mCropVideoView?.setAspectRatio(4,5)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_youtube_16_9-> {
                mCropVideoView?.setFixedAspectRatio(true)
                mCropVideoView?.setAspectRatio(16, 9)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_youtube_9_16-> {
                mCropVideoView?.setFixedAspectRatio(true)
                mCropVideoView?.setAspectRatio(9,16)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_tiktok_16_9-> {
                mCropVideoView?.setFixedAspectRatio(true)
                mCropVideoView?.setAspectRatio(16, 9)
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
            }
        }

    }
}
