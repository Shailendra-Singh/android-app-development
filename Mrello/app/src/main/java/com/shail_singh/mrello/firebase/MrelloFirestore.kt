package com.shail_singh.mrello.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.BaseActivity
import com.shail_singh.mrello.activities.CardDetailsActivity
import com.shail_singh.mrello.activities.CreateBoardActivity
import com.shail_singh.mrello.activities.MainActivity
import com.shail_singh.mrello.activities.MembersActivity
import com.shail_singh.mrello.activities.ProfileActivity
import com.shail_singh.mrello.activities.TaskActivity
import com.shail_singh.mrello.activities.auth.SignInActivity
import com.shail_singh.mrello.activities.auth.SignUpActivity
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

    fun getBoardsList(activity: MainActivity) {
        firestore.collection(Constants.FIRESTORE_BOARDS_COLLECTION_NAME).whereArrayContains(
            Constants.ASSIGNED_TO, getCurrentId()
        ).get().addOnSuccessListener { document ->
            val boardList: ArrayList<MrelloBoard> = ArrayList()
            for (doc in document.documents) {
                val board = doc.toObject(MrelloBoard::class.java)
                board?.id = doc.id
                boardList.add(board!!)
            }

            activity.populateBoardsListAdapter(boardList)
        }.addOnFailureListener {
            Log.e(
                activity.javaClass.simpleName,
                activity.resources.getString(R.string.error_firestore_fetch_collection)
            )
            it.printStackTrace()
            activity.dismissProgressDialog()
        }
    }

    fun getAssignedMembersListDetails(activity: BaseActivity, assignedTo: List<String>) {
        firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME)
            .whereIn(Constants.ID, assignedTo).get().addOnSuccessListener { document ->
                val users: ArrayList<MrelloUser> = ArrayList()
                for (doc in document.documents) {
                    val user = doc.toObject(MrelloUser::class.java)
                    users.add(user!!)
                }
                when (activity) {
                    is MembersActivity -> activity.populateMembersListAdapter(users)
                    is TaskActivity -> activity.onBoardMembersDetailsSuccess(users)
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

    fun assignMemberToBoard(activity: MembersActivity, board: MrelloBoard, user: MrelloUser) {
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        firestore.collection(Constants.FIRESTORE_BOARDS_COLLECTION_NAME).document(board.id)
            .update(assignedToHashMap).addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_add_collection)
                )
                it.printStackTrace()
                activity.dismissProgressDialog()
            }
    }

    fun getBoard(activity: TaskActivity, boardId: String) {
        firestore.collection(Constants.FIRESTORE_BOARDS_COLLECTION_NAME).document(boardId).get()
            .addOnSuccessListener { document ->
                val board: MrelloBoard = document.toObject(MrelloBoard::class.java)!!
                board.id = document.id
                activity.onBoardDetailsSuccess(board)
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_fetch_collection)
                )
                it.printStackTrace()
                activity.dismissProgressDialog()
            }
    }

    fun addUpdateTaskList(activity: BaseActivity, board: MrelloBoard) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        firestore.collection(Constants.FIRESTORE_BOARDS_COLLECTION_NAME).document(board.id)
            .update(taskListHashMap).addOnSuccessListener {
                when (activity) {
                    is TaskActivity -> {
                        activity.onTaskCreatedSuccess()
                    }

                    is CardDetailsActivity -> {
                        activity.onTaskCreatedSuccess()
                    }
                }
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    activity.resources.getString(R.string.error_firestore_add_collection)
                )
                it.printStackTrace()
                activity.dismissProgressDialog()
            }
    }

    fun loadUserData(activity: BaseActivity, readBoardsList: Boolean) {
        firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME).document(getCurrentId())
            .get().addOnSuccessListener { document ->
                val loggedInUser = document.toObject(MrelloUser::class.java)
                loggedInUser?.let {
                    when (activity) {
                        is SignInActivity -> activity.onUserLoginSuccess()
                        is MainActivity -> activity.updateNavigationUserDetails(
                            loggedInUser, readBoardsList
                        )

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
        activity: BaseActivity, userUpdatesHashMap: HashMap<String, Any>?
    ) {
        if (!userUpdatesHashMap.isNullOrEmpty()) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME).document(getCurrentId())
                .update(userUpdatesHashMap).addOnSuccessListener {
                    when (activity) {
                        is ProfileActivity -> {
                            activity.updateProfileDataSuccess()
                            activity.showInfoToast(activity.resources.getString(R.string.profile_update_success))
                        }

                        is MainActivity -> {
                            activity.tokenUpdateSuccess()
                        }
                    }

                }.addOnFailureListener {
                    activity.showErrorSnackBar(activity.resources.getString(R.string.profile_update_failure))
                    Log.e("PROFILE UPDATE", it.toString())
                    it.printStackTrace()
                }

        } else {
            // Return! No need to call FireStore
            activity.updateProfileDataSuccess()
            activity.showInfoToast(activity.resources.getString(R.string.info_no_changes))
        }
    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        firestore.collection(Constants.FIRESTORE_USER_COLLECTION_NAME)
            .whereEqualTo(Constants.EMAIL, email).get().addOnSuccessListener { document ->
                if (document.size() > 0) {
                    val user = document.documents[0].toObject(MrelloUser::class.java)
                    activity.memberDetail(user!!)
                } else {
                    activity.dismissProgressDialog()
                    activity.showErrorSnackBar(activity.resources.getString(R.string.error_no_such_member))
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

    fun getCurrentId(): String {
        val currentUser = firebase.currentUser
        return when {
            currentUser != null -> currentUser.uid
            else -> ""
        }
    }
}