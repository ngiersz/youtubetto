package com.example.youtubetto

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.youtube.YouTubeScopes;

import com.google.api.services.youtube.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import androidx.annotation.NonNull
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.ChannelListResponse





class MainActivity : YouTubeBaseActivity(), EasyPermissions.PermissionCallbacks {
    private val video_id = "IGQBtbKSVhY"
    private var youtube_api_key: String = BuildConfig.YOUTUBE_API_KEY

    private lateinit var youtubePlayer: YouTubePlayerView
    private lateinit var btnPlayer: Button

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    var mCredential: GoogleAccountCredential? = null
    private var mOutputText: TextView? = null
    private var mCallApiButton: Button? = null
    var mProgress: ProgressDialog? = null

    val REQUEST_ACCOUNT_PICKER = 1000
    val REQUEST_AUTHORIZATION = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

    private val BUTTON_TEXT = "Call YouTube Data API"
    private val PREF_ACCOUNT_NAME = "accountName"
    private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        activityLayout.layoutParams = lp
        activityLayout.orientation = LinearLayout.VERTICAL
        activityLayout.setPadding(16, 16, 16, 16)

        val tlp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mCallApiButton = Button(this)
        mCallApiButton!!.setText(BUTTON_TEXT)
        mCallApiButton!!.setOnClickListener(View.OnClickListener {
            mCallApiButton!!.setEnabled(false)
            mOutputText!!.text = ""
            getResultsFromApi()
            mCallApiButton!!.setEnabled(true)
        })
        activityLayout.addView(mCallApiButton)

        mOutputText = TextView(this)
        mOutputText!!.setLayoutParams(tlp)
        mOutputText!!.setPadding(16, 16, 16, 16)
        mOutputText!!.setVerticalScrollBarEnabled(true)
        mOutputText!!.setMovementMethod(ScrollingMovementMethod())
        mOutputText!!.setText("Click the \'$BUTTON_TEXT\' button to test the API.")
        activityLayout.addView(mOutputText)

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Calling YouTube Data API ...")

        setContentView(activityLayout)

        // Initialize credentials and service object.

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, Arrays.asList<String>(*SCOPES)
        ).setBackOff(ExponentialBackOff())

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

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS
            )
        ) {
            val accountName = getPreferences(MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential!!.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                    mCredential!!.newChooseAccountInt§ent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(

                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
                mOutputText!!.text = "This app requires Google Play Services. Please install " +
                        "Google Play Services on your device and relaunch this app."
            } else {
                getResultsFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == RESULT_OK && data != null && data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    mCredential!!.selectedAccountName = accountName
                    getResultsFromApi()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == RESULT_OK) {
                getResultsFromApi()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this
        )
    }

    override fun onPermissionsGranted(requestCode: Int, list: kotlin.collections.List<String?>?) {
        // Do nothing.
    }

    override fun onPermissionsDenied(requestCode: Int, list: kotlin.collections.List<String?>?) {
        // Do nothing.
    }

    private fun isDeviceOnline(): Boolean {
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(
        connectionStatusCode: Int
    ) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this@MainActivity,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    private class MakeRequestTask : AsyncTask<Void, Void, List<String>> {
        var mService: YouTube? = null
        var mLastError: Exception? = null

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
            .setApplicationName("YouTube Data API Android Quickstart")
                .build();
        }

        override fun doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Throws(IOException::class)
        private fun getDataFromApi(): kotlin.collections.List<String>? {
            // Get a list of up to 10 files.
            val channelInfo: MutableList<String> = ArrayList()
            val result = mService!!.channels().list("snippet,contentDetails,statistics")
                .setForUsername("GoogleDevelopers")
                .execute()
            val channels = result.items
            if (channels != null) {
                val channel = channels[0]
                channelInfo.add(
                    "This channel's ID is " + channel.id.toString() + ". " +
                            "Its title is '" + channel.snippet.title.toString() + ", " +
                            "and it has " + channel.statistics.viewCount.toString() + " views."
                )
            }
            return channelInfo
        }

        override fun onPreExecute() {
            mOutputText.setText("")
            mProgress.show()
        }

        protected fun onPostExecute(output: kotlin.collections.List<String?>?) {
            mProgress.hide()
            if (output == null || output.size == 0) {
                mOutputText.setText("No results returned.")
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:")
                mOutputText.setText(TextUtils.join("\n", output))
            }
        }
    }

}



