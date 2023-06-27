package com.shail_singh.mrello.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
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
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloCard

class CardDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var board: MrelloBoard
    private lateinit var card: MrelloCard
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
        this.card = this.board.taskList[this.taskListPosition].cardList[this.cardPosition]
        supportActionBar?.title = card.name

        binding.btnUpdate.setOnClickListener {
            this.updateCardDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        // Initialize UI Elements
        binding.etCardName.setText(this.card.name)
        binding.etCardName.setSelection(binding.etCardName.text.toString().length)
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