package com.shail_singh.mrello.activities

import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.shail_singh.mrello.R

open class AuthActivity : BaseActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                super.showErrorSnackBar(resources.getString(R.string.error_empty_email))
                false
            }

            TextUtils.isEmpty(password) -> {
                super.showErrorSnackBar(resources.getString(R.string.error_empty_password))
                false
            }

            else -> true
        }
    }

    fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                super.showErrorSnackBar(resources.getString(R.string.error_empty_name))
                false
            }

            else -> validateForm(email, password)
        }
    }
}