package com.shail_singh.mrello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.mrello.R
import com.shail_singh.mrello.adapters.MemberListItemAdapter
import com.shail_singh.mrello.databinding.DialogListSelectorBinding
import com.shail_singh.mrello.models.MrelloUser

abstract class AssignedMemberListDialog(
    context: Context,
    private val members: ArrayList<MrelloUser>,
    private val selectedMemberId: String,
) : Dialog(context), MemberListItemAdapter.MemberSelectionHandler {
    private lateinit var binding: DialogListSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogListSelectorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        super.setContentView(binding.root)
        super.setCanceledOnTouchOutside(true)
        super.setCancelable(true)
        super.getWindow()?.setLayout(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )

        binding.tvLabel.text = context.resources.getString(R.string.select_members)
        binding.rvItems.layoutManager = LinearLayoutManager(context)
        val adapter = MemberListItemAdapter(context, members, true)
        adapter.setItemSelectionListener(this, selectedMemberId)
        binding.rvItems.adapter = adapter
    }

    abstract fun onMemberSelected(member: MrelloUser)
    override fun onMemberSelection(position: Int, member: MrelloUser) {
        super.dismiss()
        onMemberSelected(member)
    }
}