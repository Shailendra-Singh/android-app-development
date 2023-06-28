package com.shail_singh.mrello.adapters

import android.content.Context
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
            holder.rvSelectedMembers.layoutManager = GridLayoutManager(context, 3)
            val otherMembers: ArrayList<MrelloUser> = ArrayList()
            for (member in this.assignedToMembersList) {
                if (member.id.compareTo(card.createdBy) != 0) {
                    otherMembers.add(member)
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