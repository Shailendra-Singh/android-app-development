package com.shail_singh.mrello.firebase

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.BaseActivity
import com.shail_singh.mrello.activities.CreateBoardActivity
import com.shail_singh.mrello.activities.MainActivity
import com.shail_singh.mrello.activities.ProfileActivity
import com.shail_singh.mrello.activities.SignInActivity
import com.shail_singh.mrello.activities.SignUpActivity
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloUser

class MrelloFirestore {
    private val firestore = FirebaseFirestore.getInstance()
    private val firebase = FirebaseAuth.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: MrelloUser) {
        firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME).document(getCurrentId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.onUserRegisteredSuccess()
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_add_collection)
                )
                activity.dismissProgressDialog()
                it.printStackTrace()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: MrelloBoard) {
        firestore.collection(Constants.FIRESTORE_BOARDS_COLLECTION_NAME).document()
            .set(board, SetOptions.merge()).addOnSuccessListener {
                activity.updateBoardDataSuccess()
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_add_collection)
                )
                it.printStackTrace()
                activity.dismissProgressDialog()
            }
    }

    fun loadUserData(activity: BaseActivity) {
        firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME).document(getCurrentId())
            .get().addOnSuccessListener { document ->
                val loggedInUser = document.toObject(MrelloUser::class.java)
                loggedInUser?.let {
                    when (activity) {
                        is SignInActivity -> activity.onUserLoginSuccess()
                        is MainActivity -> activity.updateNavigationUserDetails(loggedInUser)
                        is ProfileActivity -> activity.setUserDataInUI(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_fetch_collection)
                )
                it.printStackTrace()
                activity.dismissProgressDialog()
            }
    }

    fun updateUserProfileData(
        activity: ProfileActivity, userUpdatesHashMap: HashMap<String, Any>?
    ) {

        if (!userUpdatesHashMap.isNullOrEmpty()) {

            firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME).document(getCurrentId())
                .update(userUpdatesHashMap).addOnSuccessListener {
                    activity.updateProfileDataSuccess()
                    Toast.makeText(
                        activity,
                        activity.resources.getString(R.string.profile_update_success),
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener {
                    activity.dismissProgressDialog()
                    Toast.makeText(
                        activity,
                        activity.resources.getString(R.string.profile_update_failure),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("PROFILE UPDATE", it.toString())
                    it.printStackTrace()
                }
        } else {
            // Return! No need to call FireStore
            activity.updateProfileDataSuccess()
            Toast.makeText(
                activity,
                activity.resources.getString(R.string.profile_no_changes),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun getCurrentId(): String {
        val currentUser = firebase.currentUser
        return when {
            currentUser != null -> currentUser.uid
            else -> ""
        }
    }
}