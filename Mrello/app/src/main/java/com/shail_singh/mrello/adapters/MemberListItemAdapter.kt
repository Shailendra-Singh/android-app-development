package com.shail_singh.mrello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ItemMembersListBinding
import com.shail_singh.mrello.models.MrelloUser

class MemberListItemAdapter(
    private val context: Context,
    private val members: List<MrelloUser>,
    private val showCheckMarks: Boolean = false
) : RecyclerView.Adapter<MemberListItemAdapter.ViewHolder>() {

    private var memberSelectionHandler: MemberSelectionHandler? = null
    private var assignedToMembersIsSelectedHashMap: HashMap<String, Boolean>? = null

    interface MemberSelectionHandler {
        fun onMemberSelection()
    }

    fun setItemSelectionListener(
        memberSelectionHandler: MemberSelectionHandler,
        assignedToMembersIsSelectedHashMap: HashMap<String, Boolean>
    ) {
        this.memberSelectionHandler = memberSelectionHandler
        this.assignedToMembersIsSelectedHashMap = assignedToMembersIsSelectedHashMap
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMembersListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.tvName.text = member.name
        holder.tvEmail.text = member.email
        Glide.with(context).load(member.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(holder.ivUserProfileImage)

        if (showCheckMarks && this.memberSelectionHandler != null) {
            if (this.assignedToMembersIsSelectedHashMap != null) {
                val isChecked = this.assignedToMembersIsSelectedHashMap!![member.id]!!
                if (isChecked) {
                    holder.ivSelectionBox.visibility = View.VISIBLE
                }
            }

            holder.appBarContainer.setOnClickListener {
                // reverse the polarity
                assignedToMembersIsSelectedHashMap!![member.id] =
                    !assignedToMembersIsSelectedHashMap!![member.id]!!
                memberSelectionHandler!!.onMemberSelection()
            }
        } else {
            holder.ivSelectionBox.visibility = View.GONE
        }
    }

    class ViewHolder(binding: ItemMembersListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val ivUserProfileImage: ImageView = binding.ivUserProfileImage
        internal val tvName: TextView = binding.tvName
        internal val tvEmail: TextView = binding.tvEmail
        internal val ivSelectionBox: ImageView = binding.ivSelectionBox
        internal val appBarContainer: AppBarLayout = binding.appBarContainer
    }
}