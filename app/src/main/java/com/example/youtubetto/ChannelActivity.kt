package com.example.youtubetto

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import coil.load
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChannelActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context, channel: JSONObject): Intent {
//            fun newIntent(context: Context, videoId: String, videoTitle: String): Intent {
            val detailIntent = Intent(context, ChannelActivity::class.java)

            println(channel)

            detailIntent.putExtra("channelId", channel.getString("channelId"))
            detailIntent.putExtra("channelTitle", channel.getString("title"))
            detailIntent.putExtra("thumbnailUrl", channel.getJSONObject("thumbnails").getJSONObject("medium").getString("url"))

            return detailIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        val channelId = intent.getStringExtra("channelId")
        val channelTitle = intent.getStringExtra("channelTitle")
        val thumbnailUrl = intent.getStringExtra("thumbnailUrl")

        findViewById<TextView>(R.id.channelTitle).setText(channelTitle)

        var imageView = findViewById<ImageView>(R.id.thumbnail)
        bindImage(imageView, intent.getStringExtra("thumbnailUrl"))

        println("channelId: " + channelId)
    }

    @BindingAdapter("imageUrl")
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            println("uri " + imgUri)
            imgView.load(imgUri)
        }
    }

    fun getVideos(channelId : String) {
        val publishedAfterTimestamp = getPublishedAfterTimestamp()

        val url = "https://youtube.googleapis.com/youtube/v3/search?channelId=${channelId}&part=snippet,id&order=date&publishedAfter=${publishedAfterTimestamp}"
    }

    fun getPublishedAfterTimestamp(): String {
        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone)
        calendar.add(Calendar.DAY_OF_YEAR, -7)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN)

        simpleDateFormat.timeZone = timeZone
        return simpleDateFormat.format(calendar.getTime()) + "Z"
    }
}