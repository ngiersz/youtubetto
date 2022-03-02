package com.example.youtubetto

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SubscriptionsActivity : AppCompatActivity() {

//    private lateinit var binding: ActivitySubscriptionsBinding
    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY
    private val channel_id = "UCR04zW5-H3cUJ63KzGzFTgw"
    private val auth_token = "ya29.A0ARrdaM9nNTHoOX_kPi3M2E5NTt9uP0tkYtTbv67cX0fwgrQ1ho9500rYakE1b33rW5ErKiiqblJGiCvpeYTa2QUKUz-xR4UEHaUi38W_5bEwS7YqVPsEGHbTiOuN7OqQxaKzUtw0860LYKz3fz5_JNHS4NzK"
    val number_of_subscribed_channels_chunks = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscriptions)

        val listView : ListView = findViewById(R.id.subscriptions_videos_list);
        val textView = findViewById<TextView>(R.id.text)

        val queue = Volley.newRequestQueue(this)
        val subscribed_channels_url = "https://www.googleapis.com/youtube/v3/subscriptions?mine=true&part=snippet&maxResults=50"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, subscribed_channels_url, null,
            { response ->
                val items : JSONArray = response.getJSONArray("items")
                var subscribedChannels = ArrayList<JSONObject>()

                for (i in 0 until items.length()) {
                    subscribedChannels.add(items.getJSONObject(i).getJSONObject("snippet"))
                }
//                divide to 7 chunks
                val number_of_subscribed_channels = subscribedChannels.size
                val standard_chunk_length = number_of_subscribed_channels / number_of_subscribed_channels_chunks
                var chunked_subscribed_channels = subscribedChannels.chunked(standard_chunk_length).toMutableList()

                for (i in 0 until chunked_subscribed_channels.size) {
                    println(chunked_subscribed_channels[i].toString())
//                    subscribedChannels.add(items.getJSONObject(i))
//                    println(items.getJSONObject(i).getJSONObject("snippet").getString("title"))
                }

                var firstChunkChannelTitles = ArrayList<String>()
                var firstChunkChannels = ArrayList<JSONObject>()
                for (i in 0 until chunked_subscribed_channels.first().size) {
                    firstChunkChannelTitles.add(chunked_subscribed_channels[0][i].getString("title"))
                    firstChunkChannels.add(chunked_subscribed_channels[0][i])
                }

                val adapter = ArrayAdapter(
            this,
                    android.R.layout.simple_list_item_1, firstChunkChannelTitles
                )
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedChannel = chunked_subscribed_channels[0][position]
//                    val videoId: String =  selectedVideoSnippet.getJSONObject("resourceId").getString("videoId")
//                    val title: String =  selectedVideoSnippet.getString("title")

                    val channelIntent = ChannelActivity.newIntent(this, selectedChannel)
                    startActivity(channelIntent)
                }

//                val channelId : String = firstChunkChannels[i].getJSONObject("resourceId").getString("channelId")


//                val jsonObjectRequestVideos = JsonObjectRequest(
//                https://www.googleapis.com/youtube/v3/search?key={your_key_here}&channelId={channel_id_here}&part=snippet,id&order=date&maxResults=20

//                    Request.Method.GET, "https://www.googleapis.com/youtube/v3/playlistItems?key=${youtube_api_key}&playlistId=${playlistId}&part=snippet", null,
//                    { response ->
//                        val items : JSONArray = response.getJSONArray("items")
//
//                        val videoTitles = arrayOfNulls<String>(items.length())
//                        for (i in 0 until items.length()) {
//                            val snippet : JSONObject = items.getJSONObject(i).getJSONObject("snippet")
//                            val title : String = snippet.getString("title")
//                            videoTitles[i] = title
//                        }
//
//                        val adapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_list_item_1, videoTitles
//                        )
//                        listView.adapter = adapter
//
//                        listView.setOnItemClickListener { _, _, position, _ ->
//                            val selectedVideoSnippet = items.getJSONObject(position).getJSONObject("snippet")
//                            val videoId: String =  selectedVideoSnippet.getJSONObject("resourceId").getString("videoId")
//                            val title: String =  selectedVideoSnippet.getString("title")
//
////                            val selectedVideo: Video = Video(selectedVideoSnippet.getString("videoId"), selectedVideoSnippet.getString("title"))
//                            val videoIntent = VideoActivity.newIntent(this, videoId)
////                            val videoIntent = VideoActivity.newIntent(this, VideoActivity).apply {
////                                putExtra("title", selectedVideo.getString("title"))
////                            }
//                            startActivity(videoIntent)
//                        }
//                    },
//                    { textView.text = "That didn't work!" })
//
//                queue.add(jsonObjectRequestVideos)

            },
            { textView.text = "That didn't work!" }) {
            override fun getHeaders(): MutableMap<String, String> {
//                val accessToken = getAccessToken()
                val accessToken = auth_token
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + accessToken
                Log.d("", headers.toString())
                return headers
            }
        }

        queue.add(jsonObjectRequest)

//        binding = ActivitySubscriptionsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(findViewById(R.id.toolbar))
//        binding.toolbarLayout.title = title
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, arrayListOf("video 1", "video 2", "video 3", "video 4", "video 5")
        )
        listView.adapter = adapter

    }

    fun getAccessToken(): String? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val db = DatabaseFirebase()
        var user : User? = null

        db.getUser(firebaseUser!!.uid).addOnCompleteListener { task : Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                Log.d("", task.result.toString())

                val documents : MutableList<DocumentSnapshot> = task.result.documents
                for (document in documents) {
                    user = document!!.toObject(User::class.java)!!
                    Log.d("User", document.toObject(User::class.java).toString())
                    Log.d("", document.id + " => " + document.data)
                }
            } else {
                Log.d("", "Error getting documents: ", task.exception)
            }
        }

//        val accessToken = firebaseUser.getIdToken(true).addOnCompleteListener(this) {
//
//        }
////                    "ya29.A0ARrdaM-uGGGQJBuZI2cNCtmH6w5WGzTe4QXkthoBBM_ThJLXi0qDq0tn6KAFOFc3Q6Xjoe2K2LjhUKDh63hM2TkQe4X1lPiEABlGPCe15yOLUAZ4-m3NSBcjhABDTVzmDAi_0rjcy2fgOD1xfxDRoeYvGRda"
//return user
        return user?.idToken
    }

}