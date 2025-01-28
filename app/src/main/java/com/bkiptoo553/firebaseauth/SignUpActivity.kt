package com.bkiptoo553.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bkiptoo553.firebaseauth.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : BaseActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)

        signUpBinding.tvLoginPage.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        signUpBinding.btnSignUp.setOnClickListener{registerUser()}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerUser(){
        val name = signUpBinding.etSinUpName.text.toString()
        val email = signUpBinding.etSinUpEmail.text.toString()
        val password = signUpBinding.etSinUpPassword.text.toString()

        if (validateForm(name, email, password)){
            showProgressBar()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        showToast(this, "User ID creation a SUCCESS!!")
                        hideProgressBar()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else{
                    showToast(this, "User creation NOT a success")
                    hideProgressBar()}

                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                signUpBinding.tilName.error = "Enter name"
                false
            }

            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                signUpBinding.tilEmail.error = "Enter valid email address"
                false
            }

            TextUtils.isEmpty(password) -> {
                signUpBinding.tilPassword.error = "Enter password"
                false
            }

            else -> {
                true
            }
        }
    }
}