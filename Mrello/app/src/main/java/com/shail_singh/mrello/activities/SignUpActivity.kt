package com.shail_singh.mrello.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivitySignupBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser

class SignUpActivity : AuthActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeActionBar()

        binding.btnSignUp.setOnClickListener { registerUser() }
    }

    private fun initializeActionBar() {
        setSupportActionBar(binding.activityToolbar)
        binding.activityToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Sign Up"
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }


    private fun registerUser() {
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            super.showProgressDialog(resources.getString(R.string.please_wait))
            super.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    super.dismissProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val userInfo = MrelloUser(firebaseUser.uid, name, email)
                        MrelloFirestore().registerUser(this, userInfo)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            resources.getString(R.string.error_signing_up),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("FIREBASE - Sign Up: ", task.exception.toString())
                    }
                }
        }
    }

    fun onUserRegisteredSuccess() {
        Toast.makeText(this, resources.getString(R.string.registered_success), Toast.LENGTH_LONG)
            .show()
        super.firebaseAuth.signOut()
        Log.i("FIREBASE - Sign Up: ", "Success")
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}