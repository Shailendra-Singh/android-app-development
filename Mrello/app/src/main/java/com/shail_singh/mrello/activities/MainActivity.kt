package com.shail_singh.mrello.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.splash.IntroActivity
import com.shail_singh.mrello.adapters.BoardListItemAdapter
import com.shail_singh.mrello.databinding.ActivityMainBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.ActivityResultHandler

class MainActivity : BaseActivity(), ActivityResultHandler.OnActivityResultListener {

    companion object ActivityType {
        const val PROFILE: Int = 1
        const val CREATE_BOARD: Int = 2
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var profileActivityResultHandler: ActivityResultHandler
    private lateinit var createBoardActivityResultHandler: ActivityResultHandler
    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        drawer = binding.mainDrawer
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences(
            Constants.MRELLO_SHARED_PREFERENCES, Context.MODE_PRIVATE
        )

        val isTokenUpdated: Boolean =
            sharedPreferences.getBoolean(Constants.IS_FCM_TOKEN_UPDATED, false)

        if (isTokenUpdated) {
            super.showProgressDialog(this.getString(R.string.please_wait))
            MrelloFirestore().loadUserData(this, true)
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                updateFcmToken(token)
            })
        }

        profileActivityResultHandler = ActivityResultHandler(this)
        createBoardActivityResultHandler = ActivityResultHandler(this)

        initializeActionBar()
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_my_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    this.profileActivityResultHandler.launchIntent(
                        intent, this, PROFILE
                    )
                }

                R.id.nav_sign_out -> {
                    FirebaseAuth.getInstance().signOut()

                    sharedPreferences.edit().clear().apply()

                    val intent = Intent(this, IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        val fabButton = binding.mainActivity.contentMainActivity.fabAddBoard
        fabButton.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, this.userName)
            createBoardActivityResultHandler.launchIntent(intent, this, CREATE_BOARD)
        }
    }

    private fun initializeActionBar() {
        val actionBarView = binding.mainActivity.activityToolbar
        setSupportActionBar(actionBarView)
        actionBarView.setNavigationIcon(R.drawable.ic_menu_icon_open)
        actionBarView.setNavigationOnClickListener {
            toggleDrawer()
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.boards)
        }
    }

    fun populateBoardsListAdapter(boards: List<MrelloBoard>) {
        val recyclerView = binding.mainActivity.contentMainActivity.rvBoardList
        val textViewNoRecords = binding.mainActivity.contentMainActivity.tvNoRecordsFound

        if (boards.isNotEmpty()) {
            val boardListAdapter = BoardListItemAdapter(this, boards)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = boardListAdapter
            textViewNoRecords.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            // On-Click Listener
            boardListAdapter.setOnClickListener(object :
                BoardListItemAdapter.OnAdapterItemClickListener {
                override fun onClickItem(position: Int, model: MrelloBoard) {
                    val intent = Intent(this@MainActivity, TaskActivity::class.java)
                    intent.putExtra(Constants.ID, model.id)
                    startActivity(intent)
                }
            })

        } else {
            textViewNoRecords.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

        super.dismissProgressDialog()
    }

    private fun toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(loggedInUser: MrelloUser, readBoardsList: Boolean = false) {
        super.dismissProgressDialog()
        this.userName = loggedInUser.name

        // Update header layout
        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val ivUserProfileThumbnail = findViewById<ImageView>(R.id.iv_user_profile_image_thumbnail)

        tvUserName.text = loggedInUser.name

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(ivUserProfileThumbnail)

        if (readBoardsList) {
            super.showProgressDialog(resources.getString(R.string.please_wait))
            MrelloFirestore().getBoardsList(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.doubleBackToExit()
        }
    }

    override fun onActivityResult(customActivityCode: Int, result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            when (customActivityCode) {
                PROFILE -> {
                    MrelloFirestore().loadUserData(this, true)
                }

                CREATE_BOARD -> {
                    super.showProgressDialog(resources.getString(R.string.refreshing_boards))
                    MrelloFirestore().getBoardsList(this)
                }
            }
        }
    }

    fun tokenUpdateSuccess() {
        super.dismissProgressDialog()
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(Constants.IS_FCM_TOKEN_UPDATED, true)
        editor.apply()

        super.showProgressDialog(this.getString(R.string.please_wait))
        MrelloFirestore().loadUserData(this, true)
    }

    private fun updateFcmToken(token: String) {
        val userHashMap: HashMap<String, Any> = HashMap()
        userHashMap[Constants.FCM_TOKEN] = token
        super.showProgressDialog(this.getString(R.string.please_wait))
        MrelloFirestore().updateUserProfileData(this, userHashMap)
    }
}