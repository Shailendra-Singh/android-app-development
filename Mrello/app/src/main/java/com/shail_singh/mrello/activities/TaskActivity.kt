package com.shail_singh.mrello.activities

import android.os.Bundle
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.databinding.ActivityTaskBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard

class TaskActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var board: MrelloBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.initializeActionBar(binding.activityToolbar, "")
        // Receive parent board's id
        var id = ""
        if (intent.hasExtra(Constants.ID)) id = intent.getStringExtra(Constants.ID).toString()
        getBoardDetails(id)
    }

    private fun getBoardDetails(boardId: String) {
        this.showProgressDialog()
        MrelloFirestore().getBoard(this, boardId)
    }

    fun onBoardDetailsSuccess(board: MrelloBoard) {
        this.board = board
        this.dismissProgressDialog()
        supportActionBar?.title = board.name
    }
}