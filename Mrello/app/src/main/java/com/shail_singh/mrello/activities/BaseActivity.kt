package com.shail_singh.mrello.activities

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shail_singh.mrello.R

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressed = false
    private lateinit var progressDialog: Dialog

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // <!-- Full Screen ------------------------------------------------- -->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun showProgressDialog(message: String) {
        progressDialog = Dialog(this)
        // Set screen content from layout resource.
        // This view will be inflated, adding all top-level views to the screen
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(R.layout.dialog_progress)
        // Set custom message
        progressDialog.findViewById<TextView>(R.id.global_progress_bar_message).text = message
        // Show the dialog
        progressDialog.show()
    }

    fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressed) {
            onBackPressedDispatcher.onBackPressed()
            return
        }

        this.doubleBackToExitPressed = true
        Toast.makeText(
            this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT
        ).show()

        // Reset flag after some time to avoid closing activity accidentally
        Handler(Looper.getMainLooper()).postDelayed({
            this.doubleBackToExitPressed = false
        }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this, com.google.android.material.R.color.design_default_color_error
            )
        )

        snackBar.show()
    }
}