package com.shail_singh.mrello.activities

import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth

open class AuthActivity : BaseActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email!")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password!")
                false
            }

            else -> true
        }
    }

    fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name!")
                false
            }

            else -> validateForm(email, password)
        }
    }
}