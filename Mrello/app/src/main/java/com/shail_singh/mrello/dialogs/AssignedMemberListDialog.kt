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
    private val assignedToMembersIsSelectedHashMap: HashMap<String, Boolean>,
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
        adapter.setItemSelectionListener(this, assignedToMembersIsSelectedHashMap)
        binding.rvItems.adapter = adapter
    }

    abstract fun onMemberSelected()
    override fun onMemberSelection() {
        super.dismiss()
        onMemberSelected()
    }
}