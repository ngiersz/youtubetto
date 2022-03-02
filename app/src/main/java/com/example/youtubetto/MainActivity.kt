package com.example.youtubetto

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.android.volley.toolbox.RequestFuture
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity(), CoroutineScope {
    private val RC_SIGN_IN = 1
    private val channel_id = "UC9jAyy-X65QOVZpyGu9AKHw"

    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
//    private val user: User? = null
//    private val br: BroadcastReceiver? = null
    private lateinit var auth: FirebaseAuth

    protected lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//        val queue = Volley.newRequestQueue(this)

//        BEGIN CONCURRENCY TEST
//        job = Job()

//        println("1")
//        test(queue)
//        start()

//        println("2")
//        END CONCURRENCY TEST

//        val buttonView = findViewById<Button>(R.id.go_to_sign_in_button)
        findViewById<SignInButton>(R.id.sign_in_button).setSize(SignInButton.SIZE_ICON_ONLY)
        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    val signInIntent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
        );

        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("584397805155-4koltlu6c3oovqhtm41nbbveaua9h11s.apps.googleusercontent.com")
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = findViewById<TextView>(R.id.text)

//        val listView : ListView = findViewById(R.id.videos_list);

//        val queue = Volley.newRequestQueue(this)
        val url = "https://www.googleapis.com/youtube/v3/playlists?key=${youtube_api_key}&channelId=${channel_id}&part=snippet"

//        buttonView.setOnClickListener {_ ->
//            val signInIntent = GoogleSignInActivity.newIntent(this)
//            startActivity(signInIntent)
//        }


//                to wszystko zakomentowane - pobieranie filmów z playlisty, wyświetlenie tytułów na liście, przejście do video view po kliknięciu
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                val items : JSONArray = response.getJSONArray("items")
//
//                val playlistId : String = items.getJSONObject(0).getString("id")
//                val playlistTitle : String = items.getJSONObject(0).getJSONObject("snippet").getJSONObject("localized").getString("title")
//                val channelTitle : String = items.getJSONObject(0).getJSONObject("snippet").getString("channelTitle")




//                textView.text = "Filmy z playlisty: " + playlistTitle + ", kanał: " + channelTitle
//                val jsonObjectRequestVideos = JsonObjectRequest(
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
//                            val videoIntent = VideoActivity.newIntent(this, videoId)
//                            startActivity(videoIntent)
//                        }
//                    },
//                    { textView.text = "That didn't work!" })
//
//                queue.add(jsonObjectRequestVideos)
//            },
//            { textView.text = "That didn't work!" })
//
//        queue.add(jsonObjectRequest)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("", "firebaseAuthWithGoogle:" + account.id)
                handleSignInResult(task)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        val textView = findViewById<TextView>(R.id.user_data_text)

        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
//            updateUI(account)
            firebaseAuthWithGoogle(account);

//            textView.text = account.email + " " + account.displayName

            Log.d("", account.idToken!!);
//            Log.d("", account.serverAuthCode!!);

            val user = User(googleId = account.id, email = account.email, name = account.displayName, idToken = account.idToken)
            Log.d("", user.email!!);
            val db = DatabaseFirebase()
            db.insertUserToDatabase(user)
//            val output = Intent()
//            output.putExtra("firebaseUser", firebaseUser)
//            output.putExtra("userJSON", user.toJSON())
//            setResult(RESULT_OK, output)
//            finish()
//            val subscriptionsIntent = SubscriptionsActivity.newIntent(this)
//            startActivity(subscriptionsIntent)
            val intent = Intent(this, SubscriptionsActivity::class.java).apply {}
            startActivity(intent)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
//            textView.text = e.toString()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val db = DatabaseFirebase()
                    // Sign in success, update UI with the signed-in user's information
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    Log.d("", "current user!!! = ")
                    Log.d("", firebaseUser!!.uid)
//                    db.getUser(firebaseUser!!.uid)
//                        .addOnCompleteListener(OnCompleteListener<Any> { task ->
//                            if (task.isSuccessful) {
////                                if (task.result.isEmpty()) {
////                                    val intent = Intent(
////                                        baseContext,
////                                        CompleteAccountDataActivity::class.java
////                                    )
////                                    startActivityForResult(intent, RC_COMPLETE_ACCOUNT_DATA)
////                                } else {
////                                    val intent = Intent(baseContext, MainActivity::class.java)
////                                    startActivity(intent)
////                                    finish() // destroy this activity, it's not needed anymore
////                                }
//                            } else {
//                                Log.d("", "Error getting documents: ", task.exception)
//                            }
//                        })


                    // activity for completing user info
                } else {
                    // If sign in fails, display a message to the user.
//                    Snackbar.make(
//                        findViewById(R.layout.activity_google_sign_in),
//                        "Logowanie się nie powiodło.",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
                }
            }
    }

//    fun test() {
//        println("hi")
//    }

    fun test(queue : RequestQueue) {
//        test_coroutine(queue)
        val playlists : ArrayList<String> = arrayListOf("PLJ8cMiYb3G5fzUHSJ5VPYwalS9gXNfZ4g", "PLJ8cMiYb3G5dAchmwTsFnuMu3xgEkQ0ra", "PLJ8cMiYb3G5eOodS2LhALq1f93cqnJqWI")
        val results = ArrayList<JSONArray>()

        for (i in 0 until playlists.size) {
            val data = getData(queue, playlists[i])

        }
    }

    fun test_coroutine(queue : RequestQueue) {
        val playlists : ArrayList<String> = arrayListOf("PLJ8cMiYb3G5fzUHSJ5VPYwalS9gXNfZ4g", "PLJ8cMiYb3G5dAchmwTsFnuMu3xgEkQ0ra", "PLJ8cMiYb3G5eOodS2LhALq1f93cqnJqWI")
        val results = ArrayList<JSONArray>()
//        val results = AtomicReferenceArray<ArrayList<JSONArray>>(playlists.size)
        val resultint = AtomicInteger()
        val concurrentResult = ConcurrentHashMap<String, JSONArray>()

        //for (i in 0 until playlists.size) {
//            launch {
//                println("before getData")
//                val data = getData(queue, playlists[i])
//                println("done before add " + i)
//                results.add(data)
//                println("done after add " + i)
//            }

        lifecycleScope.launch {

            val res = async { getData(queue, playlists[0]) }
            val res2 = async { getData(queue, playlists[1]) }
            val res3 = async { getData(queue, playlists[2]) }

            awaitAll(res, res2, res3)
        }
            //println("done awaiting" + i)
            resultint.incrementAndGet()
            //println("done " + i)
        //}
        println("results " + results)
//        println("results int " + resultint)
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


     fun getData(queue : RequestQueue, playlistId : String) {
        println("getData")
        val url = "https://www.googleapis.com/youtube/v3/playlistItems?key=${youtube_api_key}&playlistId=${playlistId}"
        val itemsResult = ArrayList<JSONArray>()
        var items = JSONArray()

        val future = RequestFuture.newFuture<JSONObject>()
         val jsonObjectRequest = JsonObjectRequest(
             Request.Method.GET, url, null, future, { null }
         )
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                println("got response")
//
//                items = response.getJSONArray("items")
//                itemsResult.add(items)
//                println("items" + items)
//
//            },
//            { null })

    }

//    suspend fun run_request(playlistId : String): ArrayList<JSONArray> {
//        val queue = Volley.newRequestQueue(this)
//        val url = "https://www.googleapis.com/youtube/v3/playlistItems?key=${youtube_api_key}&playlistId=${playlistId}"
//        val itemsResult = ArrayList<JSONArray>()
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                val items : JSONArray = response.getJSONArray("items")
//                itemsResult.add(items)
//                println(items)
//            },
//            { null })
//
//        queue.add(jsonObjectRequest)
//
//        println("itemsResult" + itemsResult)
//        return itemsResult
//    }
//
//    suspend fun run_world(attr : Int): Int {
//        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
////        println("attr: " + attr) // print after delay
//        return attr.times(attr);
//    }

    interface YouTubeService {
        @GET("/v3/playlistItems?key=AIzaSyC1SANnxQFJs8mv4Yqo1djkWZqUL3gB-7g&playlistId=PLJ8cMiYb3G5fzUHSJ5VPYwalS9gXNfZ4g")
        fun getPlaylist(): Call<Playlist>
//        @GET("/v3/playlistItems?key={youtubeApiKey}&playlistId={playlistId}")
//        suspend fun getPlaylist(): Call<Playlist>
//        suspend fun getPlaylist(@Path(value = "youtubeApiKey") youtubeApiKey: String, @Path(value="playlistId") playlistId: String): Playlist
    }



    fun start() {
        val contentType = "application/json".toMediaType()
        val youtubeService by lazy {
            Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/")
                .addConverterFactory(Json.asConverterFactory(contentType))
                .build().create(YouTubeService::class.java)
        }

        val playlist =  youtubeService.getPlaylist()
        println(playlist.execute())
//        lifecycleScope.launch {
//            var playlist =  youtubeService.getPlaylist()
////            var playlist =  youtubeService.getPlaylist(youtubeApiKey = "AIzaSyC1SANnxQFJs8mv4Yqo1djkWZqUL3gB-7g", playlistId = "PLJ8cMiYb3G5fzUHSJ5VPYwalS9gXNfZ4g")
//            println("playlist: "+ playlist.execute())
//        }
    }
}