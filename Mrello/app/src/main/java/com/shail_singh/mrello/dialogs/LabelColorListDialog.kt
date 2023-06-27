package com.shail_singh.mrello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.shail_singh.mrello.R
import com.shail_singh.mrello.adapters.LabelColorItemsListAdapter
import com.shail_singh.mrello.databinding.DialogListSelectorBinding

abstract class LabelColorListDialog(
    context: Context,
    private val listColors: ArrayList<String>,
    private val selectedColor: String,
) : Dialog(context), LabelColorItemsListAdapter.ColorItemClickListener {

    private lateinit var binding: DialogListSelectorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogListSelectorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        super.setContentView(binding.root)
        super.setCanceledOnTouchOutside(true)
        super.setCancelable(true)
        super.getWindow()?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)


        binding.tvLabel.text = context.resources.getString(R.string.select_label_color)
        binding.rvItems.layoutManager = LinearLayoutManager(context)
        binding.rvItems.adapter =
            LabelColorItemsListAdapter(context, listColors, selectedColor, this)
    }

    override fun onColorItemClickListener(position: Int, color: String) {
        super.dismiss()
        onColorItemSelected(color)
    }

    protected abstract fun onColorItemSelected(color: String)
}