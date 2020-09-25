package com.konkuk.select.activity

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.konkuk.select.R
import kotlinx.android.synthetic.main.activity_login_activty.*


class LoginActivty : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    lateinit var email: String
    lateinit var password: String

    val TAG = "firebase"
    val GOOGLE = "google"

    val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)
        init()
        settingOnClickListener()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Log.d(TAG, "currentUser: $currentUser")

        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d(TAG, "google account: $account")

    }

    fun init(){
        // firebase init
        auth = Firebase.auth
    }

    fun initLoginField(){
        email = email_et.text.toString()
        password = password_et.text.toString()
    }

    fun clearField(){
        email_et.text.clear()
        password_et.text.clear()
    }

    fun settingOnClickListener(){
        loginBtn.setOnClickListener {
            initLoginField()
            if(email.isNotEmpty() && password.isNotEmpty()){
                login(email, password)
            }
        }

        signInGoogleBtn.setOnClickListener {
            socialLogin(GOOGLE)
        }

        signinBtn.setOnClickListener {
            initLoginField()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                signin(email, password)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                loginSuccess()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    fun login(email: String, password: String){
        // 기존 사용자 로그인
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Log.d(TAG, "user: $user")
                    loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "user: null")
                    // ...
                }
                // ...
            }
    }

    fun socialLogin(type: String){
        when(type){
            GOOGLE -> {
                // Configure Google Sign In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso);
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            else -> {}
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d(TAG, "user: $user")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "user: null")
                }

                // ...
            }
    }

    fun signin(email: String, password: String){
        // 신규 사용자 가입
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.d("TAG", "user id: ${user?.uid}")
                    clearField()
                    Toast.makeText(this, "회원가입 완료, 로그인하세요", Toast.LENGTH_SHORT).show()
                    user?.uid?.let { addAditionalInfo(it,"고서영", "man", 1998) }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TAG", "user: null")
                }

                // ...
            }
    }

    fun addAditionalInfo(uid:String, name:String, gender: String, birthYear: Int){
        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "gender" to gender,
            "born" to birthYear
        )

        // Add a new document with a generated ID
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun loginSuccess(){
        startActivity(Intent(this, MainActivity::class.java))
    }

}