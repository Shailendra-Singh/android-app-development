package com.shail_singh.mrello.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.adapters.TaskListItemAdapter
import com.shail_singh.mrello.databinding.ActivityTaskBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.models.MrelloTask
import com.shail_singh.mrello.utils.ActivityResultHandler

class TaskActivity : BaseActivity(), TaskListItemAdapter.TaskListItemActionListener,
    ActivityResultHandler.OnActivityResultListener {

    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 1
    }

    private lateinit var binding: ActivityTaskBinding
    private lateinit var board: MrelloBoard
    private lateinit var rvTasks: RecyclerView
    private lateinit var currentUserId: String
    private lateinit var boardDocumentId: String
    private lateinit var membersActivityResultHandler: ActivityResultHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        rvTasks = binding.rvTasks
        setContentView(binding.root)
        super.initializeActionBar(binding.activityToolbar, "")

        // Receive parent board's id
        this.boardDocumentId = ""
        if (intent.hasExtra(Constants.ID)) this.boardDocumentId =
            intent.getStringExtra(Constants.ID).toString()

        getBoardDetails(this.boardDocumentId)

        this.membersActivityResultHandler = ActivityResultHandler(this)

        this.currentUserId = MrelloFirestore().getCurrentId()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_members_menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, this.board)
                this.membersActivityResultHandler.launchIntent(intent, this, MEMBERS_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun saveTaskListToBoard() {
        super.showProgressDialog(resources.getString(R.string.please_wait))
        MrelloFirestore().addUpdateTaskList(this, this.board)
    }

    fun onTaskCreatedSuccess() {
        this.dismissProgressDialog()

    }

    override fun actionCardAdded(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!

        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)

        /* Save to Firestore */
        saveTaskListToBoard()

        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyItemChanged(position, task)
        super.showInfoToast(resources.getString(R.string.card_added))
    }

    override fun actionListEdited(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!

        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)

        /* Save to Firestore */
        saveTaskListToBoard()

        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyItemChanged(position, task)

        super.showInfoToast(resources.getString(R.string.list_edited))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun actionListDeleted(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!

        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)

        // remove the current item
        board.taskList.removeAt(position)

        /* Save to Firestore */
        saveTaskListToBoard()

        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyDataSetChanged()

        super.showInfoToast("${task.name} deleted")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun actionListAdded(position: Int, task: MrelloTask) {
        val adapter = rvTasks.adapter!!

        // remove the dummy item
        board.taskList.removeAt(adapter.itemCount - 1)
        // add new item at the starting of the list
        board.taskList.add(0, task)

        /* Save to Firestore */
        saveTaskListToBoard()

        // add the dummy item back
        board.taskList.add(MrelloTask())

        adapter.notifyDataSetChanged()
        super.showInfoToast("${task.name} added")
    }

    override fun onActivityResult(customActivityCode: Int, result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            when (customActivityCode) {
                MEMBERS_REQUEST_CODE -> {
                    getBoardDetails(this.boardDocumentId)
                }
            }
        }
    }
}