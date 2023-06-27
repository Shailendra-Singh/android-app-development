package com.shail_singh.mrello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.shail_singh.mrello.adapters.LabelColorItemsListAdapter
import com.shail_singh.mrello.databinding.DialogListColorSelectorBinding

abstract class LabelColorListDialog(
    context: Context,
    private val listColors: ArrayList<String>,
    private val selectedColor: String,
) : Dialog(context), LabelColorItemsListAdapter.ColorItemClickListener {

    private lateinit var binding: DialogListColorSelectorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogListColorSelectorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        super.setContentView(binding.root)
        super.setCanceledOnTouchOutside(true)
        super.setCancelable(true)

        binding.rvLabelColors.layoutManager = LinearLayoutManager(context)
        binding.rvLabelColors.adapter =
            LabelColorItemsListAdapter(context, listColors, selectedColor, this)
    }

    override fun onColorItemClickListener(position: Int, color: String) {
        super.dismiss()
        onColorItemSelected(color)
    }

    protected abstract fun onColorItemSelected(color: String)
}