package com.fx.fxcropper.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
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
    val isPlaying: Boolean
        get() = player.playWhenReady

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
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.addListener(this)
    }

    fun initMediaSource(context: Context, uri: String) {
        val dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayer"))
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = ExtractorMediaSource(
            Uri.parse(uri),
            dataSourceFactory,
            extractorsFactory,
            android.os.Handler(),
            ExtractorMediaSource.EventListener { }
        )

        player.prepare(videoSource)
        player.addVideoListener(this)
    }

    fun play(play: Boolean) {
        player.playWhenReady = play
    }


    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException) {

    }

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    override fun onSeekProcessed() {

    }

    override fun onScrubStart(timeBar: TimeBar, position: Long) {

    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        seekTo(position)
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        seekTo(position)
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
    }

    override fun onRenderedFirstFrame() {

    }

}
