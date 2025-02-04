package com.bkiptoo553.firebaseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bkiptoo553.firebaseauth.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private lateinit var signInBinding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.gcm_defaultSenderId))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        signInBinding.tvForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgetPasswordActivity::class.java))

        }

        signInBinding.tvRegister.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        signInBinding.btnSignIn.setOnClickListener{
            signInUser()
        }

        signInBinding.btnSignInWithGoogle.setOnClickListener { signInWithGoogle() }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signInUser(){
        val email = signInBinding.etSinInEmail.text.toString()
        val password = signInBinding.etSinInPassword.text.toString()
        if(validateForm(email, password)){
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        hideProgressBar()
                    }else{
                        showToast(this, "Login Unsuccessful", )
                        hideProgressBar()
                    }
                }
        }
    }

    private fun signInWithGoogle(){
        val signIntent = googleSignInClient.signInIntent
        launcher.launch(signIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account!=null){
                updateUI(account)
            }
        }else{
            showToast(
                this,
                "SignIn failed"
            )
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                hideProgressBar()
            }else{
                showToast(this, "Login Unsuccessful", )
                hideProgressBar()
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                signInBinding.tilEmail.error = "Enter valid email address"
                false
            }

            TextUtils.isEmpty(password) -> {
                signInBinding.tilPassword.error = "Enter password"
                false
            }

            else -> {
                true
            }
        }
    }
}