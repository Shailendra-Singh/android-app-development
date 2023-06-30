package com.shail_singh.mrello.adapters

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.TaskActivity
import com.shail_singh.mrello.databinding.ItemTaskListBinding
import com.shail_singh.mrello.databinding.LayoutDialogDeleteItemBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloCard
import com.shail_singh.mrello.models.MrelloTask
import com.shail_singh.mrello.models.MrelloUser
import java.util.Collections

class TaskListItemViewHolder(
    private val context: Context,
    private val binding: ItemTaskListBinding,
    private val taskListItemActionAdapter: TaskListItemAdapter.TaskListItemActionListener,
    private val cardClickListener: TaskCardItemAdapter.CardClickListener,
    private val assignedToMembersList: ArrayList<MrelloUser>
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var position: Int = 0
    private lateinit var task: MrelloTask
    private val currentId = MrelloFirestore().getCurrentId()

    /* Card-Drag Variables*/
    private var cardPositionDraggedFrom: Int = -1
    private var cardPositionDraggedTo: Int = -1


    /* UI Variables*/

    // Buttons
    internal lateinit var btnAddList: FrameLayout
    internal lateinit var btnAddCard: FrameLayout
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
    internal lateinit var tvList: TextView
    private lateinit var dividerH1: View
    private lateinit var dividerH2: View

    private lateinit var rvCards: RecyclerView

    // Containers
    private lateinit var containerAddList: ConstraintLayout
    private lateinit var containerEditList: ConstraintLayout
    internal lateinit var containerList: ConstraintLayout
    private lateinit var containerAddCard: ConstraintLayout

    init {
        // Initialize UI Variables
        initializeUIVariables()
    }

    fun initializeListenersAndVariables(position: Int, task: MrelloTask) {
        this.position = position
        this.task = task

        rvCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvCards.setHasFixedSize(true)
        val taskCardItemAdapter = TaskCardItemAdapter(
            context,
            task.cardList,
            cardClickListener,
            taskPosition = position,
            assignedToMembersList
        )
        rvCards.adapter = taskCardItemAdapter

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


        // Cards drag up and down functionality
        val dividerItemDecoration = DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL
        )
        rvCards.addItemDecoration(dividerItemDecoration)
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                if (cardPositionDraggedFrom == -1) {
                    cardPositionDraggedFrom = draggedPosition
                }
                cardPositionDraggedTo = targetPosition
                Collections.swap(task.cardList, draggedPosition, targetPosition)
                rvCards.adapter?.notifyItemMoved(draggedPosition, targetPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun clearView(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (cardPositionDraggedFrom != -1 && cardPositionDraggedTo != -1 && cardPositionDraggedFrom != cardPositionDraggedTo) {
                    taskListItemActionAdapter.actionCardAddedOrUpdated(position, task)
                }

                cardPositionDraggedFrom = -1
                cardPositionDraggedTo = -1
            }
        })

        touchHelper.attachToRecyclerView(rvCards)
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
        dividerH1 = binding.dividerH1
        dividerH2 = binding.dividerH2

        rvCards = binding.rvCards

        // Containers
        containerAddList = binding.containerAddList
        containerEditList = binding.containerEditList
        containerList = binding.containerList
        containerAddCard = binding.containerAddCard
    }

    fun resetView() {
        if (rvCards.adapter?.itemCount == 0) {
            rvCards.visibility = View.GONE
            dividerH2.visibility = View.GONE
        } else {
            rvCards.visibility = View.VISIBLE
            dividerH2.visibility = View.VISIBLE
        }

        containerAddList.visibility = View.GONE
        containerEditList.visibility = View.GONE
        containerList.visibility = View.GONE
        containerAddCard.visibility = View.GONE
    }

    private fun clearInputs() {
        etAddList.text?.clear()
        etEditList.text?.clear()
        etAddCard.text?.clear()
    }

    private fun validateInput(listTitle: String?) = !TextUtils.isEmpty(listTitle)

    override fun onClick(v: View?) {
        when (v!!.id) {
            btnAddList.id -> {
                btnAddList.visibility = View.GONE
                containerAddList.visibility = View.VISIBLE

            }

            btnListAddCancel.id -> {
                btnAddList.visibility = View.VISIBLE
                containerAddList.visibility = View.GONE

                clearInputs()
            }

            btnListAddOk.id -> {
                val listName = etAddList.text.toString()
                if (validateInput(listName)) {
                    task.name = listName
                    task.createdBy = currentId
                    tvList.text = listName

                    containerAddList.visibility = View.GONE
                    containerList.visibility = View.VISIBLE
                    rvCards.visibility = View.VISIBLE
                    btnAddCard.visibility = View.VISIBLE

                    clearInputs()
                    taskListItemActionAdapter.actionListAdded(position, task)
                }
            }

            btnListEdit.id -> {
                containerList.visibility = View.GONE
                containerEditList.visibility = View.VISIBLE
                rvCards.visibility = View.VISIBLE
                btnAddCard.isClickable = false

                etEditList.setText(task.name)
            }

            btnListEditCancel.id -> {
                containerList.visibility = View.VISIBLE

                rvCards.visibility = View.VISIBLE
                containerEditList.visibility = View.GONE
                btnAddCard.isClickable = true

                clearInputs()
            }

            btnListEditOk.id -> {
                val listName = etEditList.text.toString()
                if (validateInput(listName)) {
                    task.name = listName
                    task.createdBy = currentId
                    tvList.text = listName

                    containerEditList.visibility = View.GONE
                    containerList.visibility = View.VISIBLE
                    rvCards.visibility = View.VISIBLE
                    btnAddCard.visibility = View.VISIBLE

                    clearInputs()
                    taskListItemActionAdapter.actionListEdited(position, task)
                }
            }

            btnListDelete.id -> {
                val dialog = Dialog(context)
                val dialogBinding =
                    LayoutDialogDeleteItemBinding.inflate((context as TaskActivity).layoutInflater)
                "Delete ${this.task.name}?".also { dialogBinding.tvActionTitle.text = it }
                dialogBinding.tvActionDescription.text =
                    context.getString(R.string.task_delete_alert)
                dialog.setContentView(dialogBinding.root)
                dialogBinding.btnDelete.setOnClickListener {
                    dialog.dismiss()
                    taskListItemActionAdapter.actionListDeleted(position, task)
                }
                dialogBinding.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
                dialog.show()
            }

            btnAddCard.id -> {
                btnAddCard.visibility = View.GONE
                containerAddCard.visibility = View.VISIBLE
            }

            btnCardAddCancel.id -> {
                btnAddCard.visibility = View.VISIBLE
                containerAddCard.visibility = View.GONE
                clearInputs()
            }

            btnCardAddOk.id -> {
                val cardName = etAddCard.text.toString()
                if (validateInput(cardName)) {
                    containerAddCard.visibility = View.GONE
                    btnAddCard.visibility = View.VISIBLE

                    rvCards.visibility = View.VISIBLE
                    dividerH2.visibility = View.VISIBLE

                    val card = MrelloCard(cardName, currentId)
                    card.assignedTo.add(currentId)
                    task.cardList.add(card)
                    rvCards.adapter?.notifyItemInserted(task.cardList.size - 1)

                    clearInputs()
                    taskListItemActionAdapter.actionCardAddedOrUpdated(position, task)
                }
            }
        }
    }
}