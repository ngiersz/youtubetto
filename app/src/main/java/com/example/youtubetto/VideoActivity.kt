package com.example.youtubetto

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.android.youtube.player.YouTubeInitializationResult


class VideoActivity : YouTubeBaseActivity() {
    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY
    private lateinit var youtubePlayer: YouTubePlayerView
    private lateinit var btnPlayer: Button

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    companion object {
        fun newIntent(context: Context, videoId: String): Intent {
//            fun newIntent(context: Context, videoId: String, videoTitle: String): Intent {
            val detailIntent = Intent(context, VideoActivity::class.java)


            detailIntent.putExtra("videoId", videoId)
//            detailIntent.putExtra(EXTRA_URL, recipe.instructionUrl)

            return detailIntent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        youtubePlayer = findViewById(R.id.youtubePlayer)
        btnPlayer = findViewById(R.id.btnPlay)

        val videoId = intent.getStringExtra("videoId")
        val idTextView = findViewById<TextView>(R.id.videoId)
        idTextView.text = videoId


        youtubePlayerInit = object  : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                p1.loadVideo(videoId)
                p1.setPlayerStateChangeListener(object:YouTubePlayer.PlayerStateChangeListener{
                    override fun onLoading() {}
                    override fun onLoaded(p0: String?) {}
                    override fun onAdStarted() {}
                    override fun onVideoStarted() {}
                    override fun onVideoEnded() {
                        p1.cueVideo(videoId)
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