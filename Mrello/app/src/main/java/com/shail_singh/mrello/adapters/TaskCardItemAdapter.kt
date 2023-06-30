package com.shail_singh.mrello.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDivider
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ItemTaskCardBinding
import com.shail_singh.mrello.models.MrelloCard
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.Utilities.toDp
import com.shail_singh.mrello.utils.Utilities.toPx

class TaskCardItemAdapter(
    private val context: Context,
    private val cards: List<MrelloCard>,
    private val cardClickListener: CardClickListener,
    private val taskPosition: Int,
    private var assignedToMembersList: ArrayList<MrelloUser> = ArrayList()
) : RecyclerView.Adapter<TaskCardItemAdapter.ViewHolder>() {

    private var parentWidth: Int = -1

    interface CardClickListener {
        fun onCardClickListener(taskPosition: Int, cardPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parentWidth = parent.width
        val binding = ItemTaskCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        holder.tvCardName.text = card.name
        holder.itemView.setOnClickListener {
            cardClickListener.onCardClickListener(taskPosition, position)
        }
        // Set up legend color
        if (card.labelColor.isNotEmpty()) {
            holder.cardLegend.visibility = View.VISIBLE
            holder.cardLegend.dividerColor = Color.parseColor(card.labelColor)
        }
        // Set up due date
        if (card.dueDate.isNotEmpty()) {
            holder.cardDueDate.visibility = View.VISIBLE
            "${context.getString(R.string.due)}${card.dueDate}".also {
                holder.cardDueDate.text = it
            }
        }
        // Set up recycler view
        if (this.assignedToMembersList.isNotEmpty()) {
            holder.rvSelectedMembers.visibility = View.VISIBLE
            // calculate span count from 70% width of the screen as used by task list item
            // use tiny image's size to adjust spans
            var spanCount: Int =
                ((Resources.getSystem().displayMetrics.widthPixels * 0.7).toInt() - 50.toPx()).toDp() / 50
            // avoid setting it less than 2
            if (spanCount < 2) {
                spanCount = 2
            }
            holder.rvSelectedMembers.layoutManager = GridLayoutManager(context, spanCount)
            val otherMembers: ArrayList<MrelloUser> = ArrayList()
            for (member in this.assignedToMembersList) {
                for (assignedToId in card.assignedTo) {
                    if (member.id.compareTo(assignedToId) == 0 && member.id != card.createdBy) {
                        otherMembers.add(member)
                    }
                }
            }
            holder.rvSelectedMembers.adapter = SelectedMemberListAdapter(context, otherMembers)
        }
    }

    class ViewHolder(binding: ItemTaskCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCardName: TextView = binding.tvCardName
        val cardLegend: MaterialDivider = binding.cardLegend
        val cardDueDate: TextView = binding.tvCardDueDate
        val rvSelectedMembers: RecyclerView = binding.rvSelectedMembers
    }
}