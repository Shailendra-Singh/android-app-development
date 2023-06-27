package com.shail_singh.mrello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.databinding.ItemTaskCardBinding
import com.shail_singh.mrello.models.MrelloCard

class TaskCardItemAdapter(
    private val context: Context,
    private val cards: List<MrelloCard>,
    private val cardClickListener: CardClickListener,
    private val taskPosition: Int,
) : RecyclerView.Adapter<TaskCardItemAdapter.ViewHolder>() {

    interface CardClickListener {
        fun onCardClickListener(taskPosition: Int, cardPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        holder.tvCardName.text = card.name
        holder.tvCardName.setOnClickListener {
            cardClickListener.onCardClickListener(taskPosition, position)
        }
    }

    class ViewHolder(binding: ItemTaskCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCardName: TextView = binding.tvCardName
    }
}