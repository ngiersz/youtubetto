package com.example.youtubetto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.AuthResult

import android.annotation.SuppressLint
import android.util.Log

import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.auth.AuthCredential


class GoogleSignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    private val RC_COMPLETE_ACCOUNT_DATA = 2

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private val user: User? = null
    private val br: BroadcastReceiver? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, GoogleSignInActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        val textView = findViewById<TextView>(R.id.user_data_text)

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    val signInIntent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
        );
        mAuth = FirebaseAuth.getInstance()
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val textView = findViewById<TextView>(R.id.user_data_text)

        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
//            updateUI(account)
//            firebaseAuthWithGoogle(account);

            textView.text = account.email + " " + account.displayName

            Log.d("", account.id!!);
            Log.d("", account.email!!);
            Log.d("", account.displayName!!);

            val user = User(googleId = account.id, email = account.email, name = account.displayName)
            Log.d("", user.email!!);
            val db = DatabaseFirebase()
            db.insertUserToDatabase(user)
//            val output = Intent()
//            output.putExtra("firebaseUser", firebaseUser)
//            output.putExtra("userJSON", user.toJSON())
//            setResult(RESULT_OK, output)
//            finish()

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
            textView.text = e.toString()
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
                    db.getUser(firebaseUser!!.uid)
                        .addOnCompleteListener(OnCompleteListener<Any> { task ->
                            if (task.isSuccessful) {
//                                if (task.result.isEmpty()) {
//                                    val intent = Intent(
//                                        baseContext,
//                                        CompleteAccountDataActivity::class.java
//                                    )
//                                    startActivityForResult(intent, RC_COMPLETE_ACCOUNT_DATA)
//                                } else {
//                                    val intent = Intent(baseContext, MainActivity::class.java)
//                                    startActivity(intent)
//                                    finish() // destroy this activity, it's not needed anymore
//                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.exception)
                            }
                        })


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
}