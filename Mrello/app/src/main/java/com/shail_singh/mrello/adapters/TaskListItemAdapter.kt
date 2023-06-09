package com.shail_singh.mrello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.databinding.ItemTaskListBinding
import com.shail_singh.mrello.models.MrelloTask
import com.shail_singh.mrello.models.MrelloUser

class TaskListItemAdapter(
    private val context: Context,
    private val tasks: List<MrelloTask>,
    private val taskListItemActionListener: TaskListItemActionListener,
    private val cardClickListener: TaskCardItemAdapter.CardClickListener,
    private val assignedToMembersList: ArrayList<MrelloUser> = ArrayList()
) : RecyclerView.Adapter<TaskListItemViewHolder>() {

    interface TaskListItemActionListener {
        fun actionCardAddedOrUpdated(position: Int, task: MrelloTask)
        fun actionListEdited(position: Int, task: MrelloTask)
        fun actionListDeleted(position: Int, task: MrelloTask)
        fun actionListAdded(position: Int, task: MrelloTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListItemViewHolder {
        val binding = ItemTaskListBinding.inflate(LayoutInflater.from(context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LayoutParams.WRAP_CONTENT
        )
        binding.root.layoutParams = layoutParams

        return TaskListItemViewHolder(
            context, binding, taskListItemActionListener, cardClickListener, assignedToMembersList
        )
    }

    override fun onBindViewHolder(holder: TaskListItemViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvList.text = task.name
        holder.initializeListenersAndVariables(position, task)
        holder.resetView()

        // Only for last item, show add option
        if (position == tasks.size - 1) {
            holder.btnAddList.visibility = View.VISIBLE

            holder.containerList.visibility = View.GONE
            holder.btnAddCard.visibility = View.GONE
        } else {
            holder.btnAddList.visibility = View.GONE

            holder.containerList.visibility = View.VISIBLE
            holder.btnAddCard.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}