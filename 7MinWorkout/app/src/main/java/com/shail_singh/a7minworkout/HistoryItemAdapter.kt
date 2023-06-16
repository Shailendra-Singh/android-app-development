package com.shail_singh.a7minworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.a7minworkout.databinding.HistoryRecordLayoutBinding

class HistoryItemAdapter(
    private val items: List<HistoryEntity>
) : RecyclerView.Adapter<HistoryItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryRecordLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvPosition.text = (itemCount - position).toString()
        holder.tvDate.text = item.date

        if (position % 2 == 0) holder.llHistoryItemContainer.setBackgroundColor(Color.LTGRAY)
        else holder.llHistoryItemContainer.setBackgroundColor(Color.WHITE)
    }

    class ViewHolder(binding: HistoryRecordLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val llHistoryItemContainer = binding.llHistoryItemContainer
        val tvPosition = binding.tvPosition
        val tvDate = binding.tvDate
    }
}