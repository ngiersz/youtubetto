package com.example.youtubetto

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class MainActivity : YouTubeBaseActivity() {
    private val video_id = "IGQBtbKSVhY"
    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY

    private lateinit var youtubePlayer: YouTubePlayerView
    private lateinit var btnPlayer: Button

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        youtubePlayer = findViewById(R.id.youtubePlayer)
        btnPlayer = findViewById(R.id.btnPlay)

        youtubePlayerInit = object  : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                p1.loadVideo(video_id)
                p1.setPlayerStateChangeListener(object:YouTubePlayer.PlayerStateChangeListener{
                    override fun onLoading() {}
                    override fun onLoaded(p0: String?) {}
                    override fun onAdStarted() {}
                    override fun onVideoStarted() {}
                    override fun onVideoEnded() {
                        p1.cueVideo(video_id)
                    }
                    override fun onError(p0: YouTubePlayer.ErrorReason?) {}
                })
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(applicationContext, p1?.toString(), Toast.LENGTH_LONG).show()
            }

        }
        btnPlayer.setOnClickListener {
            youtubePlayer.initialize(youtube_api_key, youtubePlayerInit)
        }
    }
}
