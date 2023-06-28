package com.shail_singh.mrello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ItemSelectedMembersBinding
import com.shail_singh.mrello.models.MrelloUser

class SelectedMemberListAdapter(
    private val context: Context,
    private val selectedMembers: List<MrelloUser>,
    private val addProfileImageViewClickListener: AddProfileImageViewClickListener
) : RecyclerView.Adapter<SelectedMemberListAdapter.ViewHolder>() {

    interface AddProfileImageViewClickListener {
        fun onAddProfileImageViewClick()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSelectedMembersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return selectedMembers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = selectedMembers[position]
        if (position == itemCount - 1) {
            Glide.with(context).load(R.drawable.baseline_add_circle_24).centerCrop()
                .placeholder(R.drawable.ic_default_profile_pic).into(holder.profileImage)

            holder.containerProfileImage.setOnClickListener {
                addProfileImageViewClickListener.onAddProfileImageViewClick()
            }
        } else {
            Glide.with(context).load(member.image).centerCrop()
                .placeholder(R.drawable.ic_default_profile_pic).into(holder.profileImage)
        }
    }


    class ViewHolder(binding: ItemSelectedMembersBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val containerProfileImage: FrameLayout = binding.flContainerUserProfileImage
        internal val profileImage: ImageView = binding.ivUserProfileImage
    }
}