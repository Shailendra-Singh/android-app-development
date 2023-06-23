package com.shail_singh.mrello.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivitySignInBinding
import com.shail_singh.mrello.firebase.MrelloFirestore

class SignInActivity : AuthActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.sign_in)
        )

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
                        MrelloFirestore().loadUserData(this)
                    } else {
                        super.showErrorSnackBar(resources.getString(R.string.error_signing_in))
                        Log.e("FIREBASE - Sign In: ", task.exception.toString())
                    }
                }
        }
    }

    fun onUserLoginSuccess() {
        Toast.makeText(this@SignInActivity, "Welcome back!", Toast.LENGTH_LONG).show()
        Log.i("FIREBASE - Sign In: ", "Success")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}