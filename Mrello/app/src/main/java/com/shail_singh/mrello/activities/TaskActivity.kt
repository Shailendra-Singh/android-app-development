package com.shail_singh.mrello.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityTaskBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard

class TaskActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var board: MrelloBoard

    /* UI Variables*/

    // Buttons
    private lateinit var btnAddList: FrameLayout
    private lateinit var btnAddCard: FrameLayout
    private lateinit var btnListAddCancel: FrameLayout
    private lateinit var btnListAddOk: FrameLayout
    private lateinit var btnListEditCancel: FrameLayout
    private lateinit var btnListEditOk: FrameLayout
    private lateinit var btnCardAddCancel: FrameLayout
    private lateinit var btnCardAddOk: FrameLayout
    private lateinit var btnListEdit: FrameLayout
    private lateinit var btnListDelete: FrameLayout

    // Inputs
    private lateinit var etAddList: TextInputEditText
    private lateinit var etEditList: TextInputEditText
    private lateinit var etAddCard: TextInputEditText

    // Display
    private lateinit var tvList: TextView
    private lateinit var rvCards: RecyclerView

    // Containers
    private lateinit var containerAddList: ConstraintLayout
    private lateinit var containerEditList: ConstraintLayout
    private lateinit var containerList: ConstraintLayout
    private lateinit var containerAddCard: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.initializeActionBar(binding.activityToolbar, "")

        // Initialize UI Variables
        initializeUIVariables()

        // Set up On click listeners
        registerOnClickListeners()

        // Receive parent board's id
        var id = ""
        if (intent.hasExtra(Constants.ID)) id = intent.getStringExtra(Constants.ID).toString()
        getBoardDetails(id)


    }

    private fun registerOnClickListeners() {
        btnAddList.setOnClickListener(this)
        btnAddCard.setOnClickListener(this)
        btnListAddCancel.setOnClickListener(this)
        btnListAddOk.setOnClickListener(this)
        btnListEditCancel.setOnClickListener(this)
        btnListEditOk.setOnClickListener(this)
        btnCardAddCancel.setOnClickListener(this)
        btnCardAddOk.setOnClickListener(this)
        btnListEdit.setOnClickListener(this)
        btnListDelete.setOnClickListener(this)
    }

    private fun getBoardDetails(boardId: String) {
        this.showProgressDialog()
        MrelloFirestore().getBoard(this, boardId)
    }

    private fun initializeUIVariables() {/* UI Variables*/

        // Buttons
        btnAddList = binding.btnAddList
        btnAddCard = binding.btnAddCard
        btnListAddCancel = binding.btnListAddCancel
        btnListAddOk = binding.btnListAddOk
        btnListEditCancel = binding.btnListEditCancel
        btnListEditOk = binding.btnListEditOk
        btnCardAddCancel = binding.btnCardAddCancel
        btnCardAddOk = binding.btnCardAddOk
        btnListEdit = binding.btnListEdit
        btnListDelete = binding.btnListDelete

        // Inputs
        etAddList = binding.etAddList
        etEditList = binding.etEditList
        etAddCard = binding.etAddCard

        // Display
        tvList = binding.tvList
        rvCards = binding.rvCards

        // Containers
        containerAddList = binding.containerAddList
        containerEditList = binding.containerEditList
        containerList = binding.containerList
        containerAddCard = binding.containerAddCard

        resetHiddenElements()
    }

    private fun resetHiddenElements() {/* Hide all UI containers when initializing */
        containerAddList.visibility = View.GONE
        containerEditList.visibility = View.GONE
        containerList.visibility = View.GONE
        containerAddCard.visibility = View.GONE
        rvCards.visibility = View.GONE

        // Hide add card button as well
        btnAddCard.visibility = View.GONE

        // Make add list btn clickable again
        btnAddList.isClickable = true
    }

    fun onBoardDetailsSuccess(board: MrelloBoard) {
        this.board = board
        this.dismissProgressDialog()
        supportActionBar?.title = board.name
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btnAddList.id -> {
                btnAddList.isClickable = false
                containerAddList.visibility = View.VISIBLE
            }

            btnListAddCancel.id -> {
                btnAddList.isClickable = true
                containerAddList.visibility = View.GONE
            }

            btnListAddOk.id -> {
                btnAddList.visibility = View.GONE
                containerAddList.visibility = View.GONE
                containerList.visibility = View.VISIBLE
                btnAddCard.visibility = View.VISIBLE

                actionListAdded()
            }

            btnListEdit.id -> {
                btnListEdit.visibility = View.INVISIBLE
                binding.dividerV.visibility = View.INVISIBLE
                btnListDelete.visibility = View.INVISIBLE

                containerEditList.visibility = View.VISIBLE
            }

            btnListDelete.id -> {
                resetHiddenElements()
                btnAddList.visibility = View.VISIBLE

                actionListDeleted()
            }

            btnListEditCancel.id -> {
                btnListEdit.visibility = View.VISIBLE
                binding.dividerV.visibility = View.VISIBLE
                btnListDelete.visibility = View.VISIBLE

                containerEditList.visibility = View.GONE
            }

            btnListEditOk.id -> {
                btnListEdit.visibility = View.VISIBLE
                binding.dividerV.visibility = View.VISIBLE
                btnListDelete.visibility = View.VISIBLE

                containerEditList.visibility = View.GONE

                actionListEdited()
            }

            btnAddCard.id -> {
                containerAddCard.visibility = View.VISIBLE
            }

            btnCardAddCancel.id -> {
                containerAddCard.visibility = View.GONE
            }

            btnCardAddOk.id -> {
                containerAddCard.visibility = View.GONE
                rvCards.visibility = View.VISIBLE

                actionCardAdded()
            }

        }
    }

    private fun actionCardAdded() {
        super.showInfoToast(resources.getString(R.string.card_added))
    }

    private fun actionListEdited() {
        super.showInfoToast(resources.getString(R.string.list_edited))
    }

    private fun actionListDeleted() {
        super.showInfoToast(resources.getString(R.string.list_deleted))
    }

    private fun actionListAdded() {
        super.showInfoToast(resources.getString(R.string.list_added))
    }
}