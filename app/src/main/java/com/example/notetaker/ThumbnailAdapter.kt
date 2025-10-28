package com.example.notetaker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notetaker.databinding.ItemThumbnailBinding

class ThumbnailAdapter(
    private var imageUris: List<Uri>,
    private val onImageClick: (Int) -> Unit,
    private val selectedIndex: Int
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val binding = ItemThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(imageUris[position], position == selectedIndex)
    }

    override fun getItemCount(): Int = imageUris.size

    fun updateImages(newImageUris: List<Uri>, newSelectedIndex: Int) {
        imageUris = newImageUris
        // notifyDataSetChanged is used for simplicity. DiffUtil would be more efficient.
        notifyDataSetChanged()
    }

    inner class ThumbnailViewHolder(private val binding: ItemThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUri: Uri, isSelected: Boolean) {
            Glide.with(binding.root.context)
                .load(imageUri)
                .placeholder(R.drawable.ic_camera_placeholder)
                .into(binding.ivThumbnail)

            binding.root.setOnClickListener {
                onImageClick(adapterPosition)
            }

            binding.root.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.md_theme_light_primary else R.color.md_theme_light_outline
            )
        }
    }
}