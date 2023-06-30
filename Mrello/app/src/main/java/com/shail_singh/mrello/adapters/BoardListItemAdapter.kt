package com.shail_singh.mrello.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ItemBoardsListBinding
import com.shail_singh.mrello.models.MrelloBoard

class BoardListItemAdapter(private val activity: Activity, private val boards: List<MrelloBoard>) :
    RecyclerView.Adapter<BoardListItemAdapter.ViewHolder>() {

    private var onAdapterItemClickListener: OnAdapterItemClickListener? = null

    interface OnAdapterItemClickListener {
        fun onClickItem(position: Int, model: MrelloBoard)
    }

    fun setOnClickListener(onAdapterItemClickListener: OnAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBoardsListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return this.boards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = this.boards[position]
        Glide.with(activity.baseContext).load(item.image).centerCrop()
            .placeholder(R.drawable.ic_space_dashboard_24).into(holder.ivBoardImage)
        holder.boardTitle.text = item.name
        holder.boardCreatedDate.text = item.createdDate
        "${activity.resources.getString(R.string.created_by)}${item.createdBy}".also {
            holder.boardCreatedBy.text = it
        }

        holder.itemView.setOnClickListener {
            if (this.onAdapterItemClickListener != null) {
                this.onAdapterItemClickListener!!.onClickItem(position, item)
            }
        }
    }

    class ViewHolder(binding: ItemBoardsListBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivBoardImage = binding.ivBoardImage
        val boardTitle = binding.tvTitle
        val boardCreatedDate = binding.tvDate
        val boardCreatedBy = binding.tvDescription
    }
}