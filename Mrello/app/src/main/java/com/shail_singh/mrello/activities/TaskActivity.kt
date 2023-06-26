package com.shail_singh.mrello.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.adapters.TaskListItemAdapter
import com.shail_singh.mrello.databinding.ActivityTaskBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloTask

class TaskActivity : BaseActivity(),
    TaskListItemAdapter.TaskListItemActionListener {//}, TaskListItemAdapter.TaskListItemActionListener {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var board: MrelloBoard
    private lateinit var rvTasks: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        rvTasks = binding.rvTasks
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

        // add dummy task to inflate recycle view
        board.taskList.add(MrelloTask())


        rvTasks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTasks.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this, board.taskList, this)
        rvTasks.adapter = adapter
    }

    override fun actionCardAdded(position: Int, task: MrelloTask) {

        super.showInfoToast(resources.getString(R.string.card_added))
    }

    override fun actionListEdited(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!
        adapter.notifyItemChanged(position, task)

        super.showInfoToast(resources.getString(R.string.list_edited))
    }

    override fun actionListDeleted(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!

        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)
        // remove the current item
        board.taskList.removeAt(position)
        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyDataSetChanged()

        super.showInfoToast("${task.name} deleted")
    }

    override fun actionListAdded(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!
        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)
        // add new item at the starting of the list
        board.taskList.add(0, task)
        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyDataSetChanged()
        super.showInfoToast("${task.name} added")
    }
}