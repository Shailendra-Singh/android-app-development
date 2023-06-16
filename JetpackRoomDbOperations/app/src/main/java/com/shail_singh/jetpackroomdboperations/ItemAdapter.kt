package com.shail_singh.jetpackroomdboperations

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.jetpackroomdboperations.databinding.RecordItemLayoutBinding

class ItemAdapter(
    private val items: List<EmployeeEntity>,
    private val updateListener: (id: Int) -> Unit,
    private val deleteListener: (id: Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecordItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        if (position % 2 == 0)
            holder.llRecordItemContainer.setBackgroundColor(Color.LTGRAY)
        else
            holder.llRecordItemContainer.setBackgroundColor(Color.WHITE)

        // Edit Action
        holder.ivEditBtn.setOnClickListener {
            updateListener.invoke(item.id)
        }

        // Delete Action
        holder.ivDeleteBtn.setOnClickListener {
            deleteListener.invoke(item.id)
        }
    }

    class ViewHolder(binding: RecordItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val llRecordItemContainer = binding.llRecordItemContainer
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail
        val ivEditBtn = binding.ivEditBtn
        val ivDeleteBtn = binding.ivDeleteBtn
    }
}