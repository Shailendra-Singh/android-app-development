package com.shail_singh.mrello.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.databinding.ItemLabelColorBinding

class LabelColorItemsListAdapter(
    private val context: Context,
    private val colors: ArrayList<String>,
    private val selectedColor: String,
    private val colorItemClickListener: ColorItemClickListener
) : RecyclerView.Adapter<LabelColorItemsListAdapter.ViewHolder>() {

    interface ColorItemClickListener {
        fun onColorItemClickListener(position: Int, color: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLabelColorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colors[position]
        holder.colorHolder.setBackgroundColor(Color.parseColor(color))
        if (selectedColor.compareTo(color) == 0) {
            holder.ivSelectionBox.visibility = View.VISIBLE
        } else {
            holder.ivSelectionBox.visibility = View.GONE
        }
        holder.colorHolder.setOnClickListener {
            colorItemClickListener.onColorItemClickListener(position, color)
        }
    }

    class ViewHolder(binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root) {
        val colorHolder: View = binding.colorHolder
        val ivSelectionBox: ImageView = binding.ivSelectionBox
    }
}