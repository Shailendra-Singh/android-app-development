package com.shail_singh.mrello.activities

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.adapters.MemberListItemAdapter
import com.shail_singh.mrello.databinding.ActivityMembersBinding
import com.shail_singh.mrello.databinding.LayoutDialogSearchMemberBinding
import com.shail_singh.mrello.fcm.NotificationHandler
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloUser

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var board: MrelloBoard
    private lateinit var rvMembersList: RecyclerView
    private lateinit var assignedMembersList: ArrayList<MrelloUser>
    private var memberActivityChangesMade: Boolean = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.members)
        )

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.board =
                    intent.getParcelableExtra(Constants.BOARD_DETAIL, MrelloBoard::class.java)!!
            } else {
                this.board = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            }
        }

        rvMembersList = binding.rvMembersList
        rvMembersList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMembersList.setHasFixedSize(true)

        super.showProgressDialog(resources.getString(R.string.please_wait))
        MrelloFirestore().getAssignedMembersListDetails(this, this.board.assignedTo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (memberActivityChangesMade) {
            setResult(Activity.RESULT_OK)
        } else {
            setResult(
                Activity.RESULT_CANCELED
            )
        }
        super.onBackPressed()
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        val dialogBinding = LayoutDialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnAdd.setOnClickListener {
            val email = dialogBinding.etMemberEmail.text.toString()
            if (!TextUtils.isEmpty(email)) {
                dialog.dismiss()
                super.showProgressDialog(resources.getString(R.string.please_wait))
                MrelloFirestore().getMemberDetails(this, email)
            } else {
                super.showErrorSnackBar(resources.getString(R.string.error_empty_email))
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            (binding.root.width * 0.8).toInt(), WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    fun populateMembersListAdapter(users: ArrayList<MrelloUser>) {
        this.assignedMembersList = users
        if (users.isNotEmpty()) {
            this.rvMembersList.adapter = MemberListItemAdapter(this, this.assignedMembersList)

            binding.rvMembersList.visibility = View.VISIBLE
            binding.tvNoRecordsFound.visibility = View.GONE
        } else {
            binding.rvMembersList.visibility = View.GONE
            binding.tvNoRecordsFound.visibility = View.VISIBLE
        }
        super.dismissProgressDialog()
    }

    fun memberDetail(user: MrelloUser) {
        super.dismissProgressDialog()
        this.board.assignedTo.add(user.id)
        this.assignedMembersList.add(user)
        super.showProgressDialog(resources.getString(R.string.please_wait))
        MrelloFirestore().assignMemberToBoard(this, this.board, user)
    }

    fun memberAssignSuccess(user: MrelloUser) {
        this.rvMembersList.adapter?.notifyItemInserted(this.rvMembersList.adapter?.itemCount!!)
        super.dismissProgressDialog()
        this.memberActivityChangesMade = true
        val token = user.fcmToken
        val title = getString(R.string.mrello_board_assigned)
        val body =
            "${this.getString(R.string.assigned_to_a_new_board)} ${this.board.createdBy}'s board: ${this.board.name}"
        NotificationHandler(this).sendNotification(token, title, body)
    }
}