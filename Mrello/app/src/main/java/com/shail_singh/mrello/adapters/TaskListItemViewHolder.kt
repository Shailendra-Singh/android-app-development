package com.shail_singh.mrello.adapters

import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ItemTaskListBinding
import com.shail_singh.mrello.models.MrelloTask


class TaskListItemViewHolder(
    private val context: Context,
    private val binding: ItemTaskListBinding,
    private val taskListItemActionAdapter: TaskListItemAdapter.TaskListItemActionListener
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var position: Int = 0
    private lateinit var task: MrelloTask


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

    fun registerOnClickListeners(position: Int, task: MrelloTask) {
        this.position = position
        this.task = task

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
    }

    fun resetView() {
        rvCards.visibility = View.GONE

        containerAddList.visibility = View.GONE
        containerEditList.visibility = View.GONE
        containerList.visibility = View.GONE
        containerAddCard.visibility = View.GONE
    }

    fun clearInputs() {
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
                val actionDialog = AlertDialog.Builder(context)
                actionDialog.setIcon(R.drawable.baseline_warning_24)
                actionDialog.setTitle("Select action")
                val pictureDialogItems = arrayOf(
                    context.resources.getString(R.string.action_delete_list),
                    context.resources.getString(R.string.action_skip_deletion)
                )
                actionDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> taskListItemActionAdapter.actionListDeleted(position, task)
                        1 -> dialog.dismiss()
                    }
                }
                actionDialog.show()
            }

        }
    }

}