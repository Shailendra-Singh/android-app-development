package com.shail_singh.mrello.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityCardDetailsBinding
import com.shail_singh.mrello.databinding.LayoutDialogDeleteItemBinding
import com.shail_singh.mrello.dialogs.AssignedMemberListDialog
import com.shail_singh.mrello.dialogs.LabelColorListDialog
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloCard
import com.shail_singh.mrello.models.MrelloUser

class CardDetailsActivity : BaseActivity() {

    private companion object {
        var COLOR_LIST: ArrayList<String> =
            arrayListOf("#43C86F", "#0C90F1", "#F72400", "#7A8089", "#D57C1D", "#770000", "#0022F8")
    }

    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var board: MrelloBoard
    private lateinit var card: MrelloCard
    private lateinit var membersDetailsList: ArrayList<MrelloUser>
    private var selectedColor: String = ""
    private var taskListPosition: Int = -1
    private var cardPosition: Int = -1
    private var hasChanges: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.card_details)
        )

        getIntentData()

        initializeUI()
    }

    private fun initializeUI() {
        this.card = this.board.taskList[this.taskListPosition].cardList[this.cardPosition]
        this.selectedColor = this.card.labelColor
        supportActionBar?.title = card.name


        binding.btnUpdate.setOnClickListener {
            this.updateCardDetails()
        }

        binding.btnSelectColor.setOnClickListener {
            val listDialog = object : LabelColorListDialog(
                this, COLOR_LIST, selectedColor = this.selectedColor
            ) {
                override fun onColorItemSelected(color: String) {
                    this@CardDetailsActivity.selectedColor = color
                    updateColorLabel()
                }
            }

            listDialog.show()
        }

        binding.btnSelectMembers.setOnClickListener {
            val memberDialog = object : AssignedMemberListDialog(
                this, this.membersDetailsList, this.card.createdBy
            ) {
                override fun onMemberSelected(member: MrelloUser) {
                    // TODO: Implement selection checklist
                }
            }

            memberDialog.show()
        }

        // Initialize UI Elements
        binding.etCardName.setText(this.card.name)
        binding.etCardName.setSelection(binding.etCardName.text.toString().length)

        if (this.selectedColor.isEmpty()) {
            binding.tvSelectedColor.text = resources.getString(R.string.select_color)
        } else {
            binding.tvSelectedColor.text = ""
            binding.btnSelectColor.setBackgroundColor(Color.parseColor(selectedColor))
        }
    }

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.board =
                    intent.getParcelableExtra(Constants.BOARD_DETAIL, MrelloBoard::class.java)!!
            } else {
                this.board = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            }
        }

        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.membersDetailsList = intent.getParcelableArrayListExtra(
                    Constants.BOARD_MEMBERS_LIST, MrelloUser::class.java
                )!!
            } else {
                this.membersDetailsList =
                    intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
            }
        }

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            this.taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }

        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            this.cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                dialogDeleteCard()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateColorLabel() {
        if (this.selectedColor.isEmpty()) {
            binding.tvSelectedColor.text = resources.getString(R.string.select_color)
        } else {
            binding.tvSelectedColor.text = ""
            binding.btnSelectColor.setBackgroundColor(Color.parseColor(this@CardDetailsActivity.selectedColor))
            this.card.labelColor = this.selectedColor
            this.hasChanges = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogDeleteCard() {
        val dialog = Dialog(this)
        val dialogBinding = LayoutDialogDeleteItemBinding.inflate(layoutInflater)
        dialogBinding.tvActionTitle.text = "Delete ${this.card.name}?"
        dialogBinding.tvActionDescription.text = getString(R.string.card_delete_alert)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnDelete.setOnClickListener {
            deleteCard()
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            (binding.root.width * 0.8).toInt(), WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun deleteCard() {
        this.board.taskList[this.taskListPosition].cardList.removeAt(this.cardPosition)

        // Remove dummy task as added by Task Activity to show add list button
        this.board.taskList.removeAt(this.board.taskList.size - 1)

        super.showProgressDialog(resources.getString(R.string.please_wait))
        super.showInfoToast("${this.card.name} ${resources.getString(R.string.deleted)}")
        MrelloFirestore().addUpdateTaskList(this@CardDetailsActivity, this.board)
    }

    private fun updateCardDetails() {
        val newName = binding.etCardName.text.toString()
        if (TextUtils.isEmpty(newName)) {
            showErrorSnackBar(resources.getString(R.string.error_empty_name))
            return
        } else if (this.card.name.compareTo(newName) != 0) {
            this.hasChanges = true
            this.card.name = newName
        }

        if (this.hasChanges) {
            // Remove dummy task as added by Task Activity to show add list button
            this.board.taskList.removeAt(this.board.taskList.size - 1)

            super.showProgressDialog(resources.getString(R.string.please_wait))
            MrelloFirestore().addUpdateTaskList(this@CardDetailsActivity, this.board)
        } else {
            super.showInfoToast(resources.getString(R.string.info_no_changes))
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    fun onTaskCreatedSuccess() {
        super.dismissProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}