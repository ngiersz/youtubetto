package com.example.youtubetto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import org.json.JSONArray
import org.json.JSONObject




class MainActivity : YouTubeBaseActivity() {
    private val video_id = "IGQBtbKSVhY"
    private val channel_id = "UC9jAyy-X65QOVZpyGu9AKHw"

    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY

    private lateinit var youtubePlayer: YouTubePlayerView
    private lateinit var btnPlayer: Button

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        youtubePlayer = findViewById(R.id.youtubePlayer)
        btnPlayer = findViewById(R.id.btnPlay)
        val textView = findViewById<TextView>(R.id.text)
        textView.text = "birb"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.googleapis.com/youtube/v3/playlists?key=${youtube_api_key}&channelId=${channel_id}"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val items : JSONArray = response.getJSONArray("items")

                val playlistId : String = items.getJSONObject(0).getString("id")

                val jsonObjectRequestVideos = JsonObjectRequest(
                    Request.Method.GET, "https://www.googleapis.com/youtube/v3/playlistItems?key=${youtube_api_key}&playlistId=${playlistId}&part=snippet", null,
                    { response ->
                        val items : JSONArray = response.getJSONArray("items")

//                        textView.text = "${playlistId}"
                        val snippet : JSONObject = items.getJSONObject(0).getJSONObject("snippet")
                        val title : String = snippet.getString("title")
                        textView.text = title
//                        val videoId : String = snippet.getString("videoId")
                    },
                    { textView.text = "That didn't work!" })

                queue.add(jsonObjectRequestVideos)

            },
            { textView.text = "That didn't work!" })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)

//        youtubePlayerInit = object  : YouTubePlayer.OnInitializedListener{
//            override fun onInitializationSuccess(
//                p0: YouTubePlayer.Provider?,
//                p1: YouTubePlayer?,
//                p2: Boolean
//            ) {
//                p1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
//                p1.loadVideo(video_id)
//                p1.setPlayerStateChangeListener(object:YouTubePlayer.PlayerStateChangeListener{
//                    override fun onLoading() {}
//                    override fun onLoaded(p0: String?) {}
//                    override fun onAdStarted() {}
//                    override fun onVideoStarted() {}
//                    override fun onVideoEnded() {
//                        p1.cueVideo(video_id)
//                    }
//                    override fun onError(p0: YouTubePlayer.ErrorReason?) {}
//                })
//            }
//
//            override fun onInitializationFailure(
//                p0: YouTubePlayer.Provider?,
//                p1: YouTubeInitializationResult?
//            ) {
//                Toast.makeText(applicationContext, p1?.toString(), Toast.LENGTH_LONG).show()
//            }

//        }
//        btnPlayer.setOnClickListener {
//            youtubePlayer.initialize(youtube_api_key, youtubePlayerInit)
//        }


    }
    private fun initializeYouTubePlayer(videoId: String) {

    }
}
