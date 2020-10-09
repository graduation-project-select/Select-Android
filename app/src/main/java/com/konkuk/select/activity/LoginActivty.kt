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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.konkuk.select.R
import com.konkuk.select.storage.SharedPrefManager
import kotlinx.android.synthetic.main.activity_login_activty.*


class LoginActivty : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    lateinit var user:FirebaseUser
    val loginSharedPrefManager = SharedPrefManager.getInstance((this))

    lateinit var email: String
    lateinit var password: String

    val LOGIN = "login"
    val SIGNUP = "signup"
    val GOOGLE = "google"

    val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)
        checkLoginStatus()
        auth = Firebase.auth // firebase init
        settingOnClickListener()
    }

    // 자동로그인 확인
    private fun checkLoginStatus(){
        if(loginSharedPrefManager.uid != "" && loginSharedPrefManager.uid != null){
            Toast.makeText(this, "uid: ${loginSharedPrefManager.uid}", Toast.LENGTH_SHORT).show()
            var mainPage = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainPage)
        }
    }

    private fun clearField(){
        email_et.text.clear()
        password_et.text.clear()
    }

    private fun settingOnClickListener(){
        // 로그인 버튼
        loginBtn.setOnClickListener {
            initLoginField()
            if(email.isEmpty()){
                Log.d(LOGIN, "email_et is empty")
            }else if(password.isEmpty()){
                Log.d(LOGIN, "password_et is empty")
            }else {
                login(email, password)
            }
        }

        // 구글 회원가입, 로그인 버튼
        signInGoogleBtn.setOnClickListener {
            socialLogin(GOOGLE)
        }

        // 회원가입 버튼
        signupBtn.setOnClickListener {
            initLoginField()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                signup(email, password)
            }
        }
    }

    private fun initLoginField(){
        email = email_et.text.toString()
        password = password_et.text.toString()
    }

    // 일반 로그인 (이메일, 비밀번호)
    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(LOGIN, "signInWithEmail: success")
                    user = auth.currentUser!!
                    loginSuccess(user.uid)
                } else {
                    // Sign in fails
                    Log.w(LOGIN, "signInWithEmail:failure", task.exception)
                }
            }
    }

    // 소셜 로그인 (구글)
    private fun socialLogin(type: String){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) { // 소셜 로그인
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(LOGIN, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(LOGIN, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(LOGIN, "signInWithCredential:success")
                    user = auth.currentUser!!
                    loginSuccess(user.uid)
                } else {
                    // Sign in fails
                    Log.w(LOGIN, "signInWithCredential:failure", task.exception)
                }
            }
    }

    // 일반 회원가입 (이메일, 비밀번호)
    private fun signup(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(SIGNUP, "createUserWithEmail:success")
                    user = auth.currentUser!!
                    Log.d(SIGNUP, "new user id: ${user.uid}")
                    clearField()
                    Toast.makeText(this, "회원가입 완료, 로그인하세요", Toast.LENGTH_SHORT).show()
                    // TODO: 회원가입 후 추가 정보 DB에 저장
                    // addAditionalInfo(user.uid,"고서영", "man", 1998)
                } else {
                    // Sign in fails
                    Log.w(SIGNUP, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    // 로그인 성공
    private fun loginSuccess(uid:String){
        loginSharedPrefManager.saveUid(uid)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // TODO: 회원가입 후 추가 정보 DB에 저장
    private fun addAditionalInfo(uid:String, name:String, gender: String, birthYear: Int){
        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "gender" to gender,
            "born" to birthYear
        )

        // Add a new document with a generated ID
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(SIGNUP, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(SIGNUP, "Error adding document", e)
            }
    }

}