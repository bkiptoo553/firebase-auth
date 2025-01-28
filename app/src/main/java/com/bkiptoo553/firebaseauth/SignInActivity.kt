package com.bkiptoo553.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bkiptoo553.firebaseauth.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private lateinit var signInBinding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)


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