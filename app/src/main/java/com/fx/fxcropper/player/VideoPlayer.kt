package com.fx.fxcropper.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.TIME_UNSET
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener

class VideoPlayer(context: Context) : Player.EventListener, TimeBar.OnScrubListener, VideoListener {
    val player: SimpleExoPlayer
    private val mUpdateListener: OnProgressUpdateListener? = null
    private val progressHandler: Handler
    private var progressUpdater: Runnable? = null


    val isPlaying: Boolean
        get() = player.getPlayWhenReady()

    init {
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            trackSelector,
            loadControl
        )
        player.setRepeatMode(Player.REPEAT_MODE_ONE)
        player.addListener(this)
        progressHandler = Handler()
    }

    fun initMediaSource(context: Context, uri: String) {
        val dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayer"))
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = ExtractorMediaSource(
            Uri.parse(uri),
            dataSourceFactory, extractorsFactory, null, null
        )

        player.prepare(videoSource)
        player.addVideoListener(this)
    }

    fun play(play: Boolean) {
        player.setPlayWhenReady(play)
        if (!play) {
            removeUpdater()
        }
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any, reason: Int) {
        updateProgress()
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        updateProgress()
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException) {

    }

    override fun onPositionDiscontinuity(reason: Int) {
        updateProgress()
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    override fun onSeekProcessed() {

    }

    override fun onScrubStart(timeBar: TimeBar, position: Long) {

    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        seekTo(position)
        updateProgress()
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        seekTo(position)
        updateProgress()
    }

    private fun updateProgress() {
        mUpdateListener?.onProgressUpdate(
            player.getCurrentPosition(),
            if (player.getDuration() === TIME_UNSET) 0L else player.getDuration(),
            player.getBufferedPosition()
        )
        initUpdateTimer()
    }

    private fun initUpdateTimer() {
        val position = player.getCurrentPosition()
        val playbackState = player.getPlaybackState()
        var delayMs: Long
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }

            removeUpdater()
            progressUpdater = Runnable { updateProgress() }

            progressHandler.postDelayed(progressUpdater, delayMs)
        }
    }

    private fun removeUpdater() {
        if (progressUpdater != null)
            progressHandler.removeCallbacks(progressUpdater)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }


    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        mUpdateListener?.onFirstTimeUpdate(player.getDuration(), player.getCurrentPosition())
    }

    override fun onRenderedFirstFrame() {

    }

    interface OnProgressUpdateListener {
        fun onProgressUpdate(currentPosition: Long, duration: Long, bufferedPosition: Long)

        fun onFirstTimeUpdate(duration: Long, currentPosition: Long)
    }
}
