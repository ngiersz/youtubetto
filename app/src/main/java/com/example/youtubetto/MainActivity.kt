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
import com.android.volley.RequestQueue
import com.android.volley.Response
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    private val channel_id = "UC9jAyy-X65QOVZpyGu9AKHw"

    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
//    private val user: User? = null
//    private val br: BroadcastReceiver? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        BEGIN CONCURRENCY TEST
        var queueTest = Volley.newRequestQueue(this) // 2
        launch(UI) { // 3
            val result = data(queueTest, "https://jsonplaceholder.typicode.com/posts/1")
            val result2 = data(queueTest, "https://jsonplaceholder.typicode.com/posts/2")
            val result3 = data(queueTest, "https://jsonplaceholder.typicode.com/posts/3")
            val result4 = data(queueTest, "https://jsonplaceholder.typicode.com/posts/4")
            val textToShow = result.await().toString() + "|||" +
                    result2.await().toString() + "|||" +
                    result3.await().toString() + "|||" +
                    result4.await().toString()
            println(textToShow)
        }
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

        val queue = Volley.newRequestQueue(this)
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

    // BEGIN CONCURRENCY TEST
    fun data(queue: RequestQueue?, url: String) = async(CommonPool) { readData(queue, url) }

    //2
    suspend fun readData(queue: RequestQueue?, url: String): JSONObject = suspendCancellableCoroutine { continuation ->
// 3
        val request = JsonObjectRequest(url, null, Response.Listener {
            println("$url DONE")
            continuation.resume(it)
        }, // 4
            Response.ErrorListener { continuation.resumeWithException(Exception(it.cause)) }) // 5
        queue?.add(request)
        continuation.invokeOnCompletion { // 6
            request.cancel()
        }
    }
    // END CONCURRENCY TEST

}
