package com.shail_singh.mrello.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivitySignInBinding

class SignInActivity : AuthActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbSignupToolbar)
        binding.tbSignupToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Sign In"
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        binding.btnSignIn.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            super.showProgressDialog(resources.getString(R.string.please_wait))
            super.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    super.dismissProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignInActivity, "Welcome back!", Toast.LENGTH_LONG)
                            .show()
                        Log.e("FIREBASE - Sign In: ", "Success")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            resources.getString(R.string.error_signing_in),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("FIREBASE - Sign In: ", task.exception.toString())
                    }
                }
        }
    }
}