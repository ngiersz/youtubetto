package com.example.youtubetto

import android.content.ClipData.newIntent
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : YouTubeBaseActivity() {
    private val channel_id = "UC9jAyy-X65QOVZpyGu9AKHw"

    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text)

        val listView : ListView = findViewById(R.id.videos_list);

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.googleapis.com/youtube/v3/playlists?key=${youtube_api_key}&channelId=${channel_id}&part=snippet"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val items : JSONArray = response.getJSONArray("items")

                val playlistId : String = items.getJSONObject(0).getString("id")
                val playlistTitle : String = items.getJSONObject(0).getJSONObject("snippet").getJSONObject("localized").getString("title")
                val channelTitle : String = items.getJSONObject(0).getJSONObject("snippet").getString("channelTitle")
                textView.text = "Filmy z playlisty: " + playlistTitle + ", kanał: " + channelTitle

                val jsonObjectRequestVideos = JsonObjectRequest(
                    Request.Method.GET, "https://www.googleapis.com/youtube/v3/playlistItems?key=${youtube_api_key}&playlistId=${playlistId}&part=snippet", null,
                    { response ->
                        val items : JSONArray = response.getJSONArray("items")

                        val videoTitles = arrayOfNulls<String>(items.length())
                        for (i in 0 until items.length()) {
                            val snippet : JSONObject = items.getJSONObject(i).getJSONObject("snippet")
                            val title : String = snippet.getString("title")
                            videoTitles[i] = title
                        }

                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1, videoTitles
                        )
                        listView.adapter = adapter

                        listView.setOnItemClickListener { _, _, position, _ ->
                            val selectedVideoSnippet = items.getJSONObject(position).getJSONObject("snippet")
                            val videoId: String =  selectedVideoSnippet.getJSONObject("resourceId").getString("videoId")
                            val title: String =  selectedVideoSnippet.getString("title")

//                            val selectedVideo: Video = Video(selectedVideoSnippet.getString("videoId"), selectedVideoSnippet.getString("title"))
                            val videoIntent = VideoActivity.newIntent(this, videoId)
//                            val videoIntent = VideoActivity.newIntent(this, VideoActivity).apply {
//                                putExtra("title", selectedVideo.getString("title"))
//                            }
                            startActivity(videoIntent)
                        }
                    },
                    { textView.text = "That didn't work!" })

                queue.add(jsonObjectRequestVideos)

            },
            { textView.text = "That didn't work!" })

        queue.add(jsonObjectRequest)
    }
}
