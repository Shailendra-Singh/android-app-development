package com.shail_singh.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.databinding.ItemHappyPlaceBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel

class HappyPlaceItemAdapter(private val happyPlacesList: List<HappyPlaceModel>) :
    RecyclerView.Adapter<HappyPlaceItemAdapter.ViewHolder>() {

    private var onAdapterItemClickListener: OnAdapterItemClickListener? = null

    fun setOnClickListener(onAdapterItemClickListener: OnAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHappyPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return happyPlacesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = happyPlacesList[position]

        holder.ivThumbnail.setImageURI(Uri.parse(item.imagePath))
        holder.tvTitle.text = item.title

        var descriptionShortened = item.description.toString()
        if (descriptionShortened.length > AppConstants.DESCRIPTION_SHORT_STRING_LENGTH) {
            descriptionShortened =
                descriptionShortened.subSequence(0, AppConstants.DESCRIPTION_SHORT_STRING_LENGTH)
                    .toString() + "\t..."
        }
        holder.tvDescription.text = descriptionShortened

        holder.itemView.setOnClickListener {
            if (this.onAdapterItemClickListener != null) {
                onAdapterItemClickListener!!.onClickCard(position, item)
            }
        }
    }

    class ViewHolder(binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val cvHappyPlaceItemCard = binding.cvHappyPlaceItemCard
        val ivThumbnail = binding.ivHappyPlaceThumbnail
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    interface OnAdapterItemClickListener {
        fun onClickCard(position: Int, model: HappyPlaceModel)
    }
}