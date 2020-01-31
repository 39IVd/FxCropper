package com.fx.fxcropper

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class VideoCropActivity : AppCompatActivity(), View.OnClickListener {
    private var mVideoView: VideoView? = null
    private var ic_play: ImageButton? = null
    private var ic_rewind : ImageButton? = null
    private var text_video_name: TextView? = null
    private var preset_default : RelativeLayout? = null
    private var preset_insta_1_1 : RelativeLayout? = null
    private var preset_insta_4_5 : RelativeLayout? = null
    private var preset_youtube_16_9 : RelativeLayout? = null
    private var preset_youtube_9_16 : RelativeLayout? = null
    private var preset_tiktok_16_9 : RelativeLayout? = null
//    private var list_presets: MutableList<RelativeLayout>? = null


    var srcFile: String? = null
    var dstFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_crop2)
        componentSetting()

        if (intent.extras != null) {
            srcFile = intent.extras.getString("EXTRA_PATH")
        }
        mVideoView!!.setVideoURI(Uri.parse(srcFile))

        var videoname = srcFile!!.substring(srcFile!!.lastIndexOf('/') + 1)
        text_video_name?.setText(videoname)

        dstFile =
            (Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + Date().time
                    + "mp4")


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

        mVideoView = findViewById(R.id.videoView) as VideoView
        mVideoView!!.setOnPreparedListener {
            mVideoView!!.seekTo(0)
        }
        mVideoView!!.setOnCompletionListener {
            mVideoView!!.seekTo(0)
            mVideoView!!.pause()
            ic_play!!.setBackgroundResource(R.drawable.ic_white_play)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ic_play-> {
                if (mVideoView!!.isPlaying()) { // stop
                    if (mVideoView != null) {
                        mVideoView!!.pause()
                        ic_play!!.setBackgroundResource(R.drawable.ic_white_play)
                    }
                } else { // play
                    if (mVideoView != null) {
                        mVideoView!!.start()
                        ic_play!!.setBackgroundResource(R.drawable.ic_white_pause)
                    }
                }
            }
            R.id.ic_rewind-> {
                mVideoView!!.seekTo(0)
//                mVideoView!!.pause()
                ic_play!!.setBackgroundResource(R.drawable.ic_white_pause)

            }
            R.id.preset_default-> {
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_insta_1_1-> {
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_insta_4_5-> {
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_youtube_16_9-> {
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_youtube_9_16-> {
                preset_default?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_1_1?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_insta_4_5?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
                preset_youtube_9_16?.setBackgroundResource(R.drawable.shape_rectangle_presets_selected)
                preset_tiktok_16_9?.setBackgroundResource(R.drawable.shape_rectangle_presets_unselected)
            }
            R.id.preset_tiktok_16_9-> {
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
