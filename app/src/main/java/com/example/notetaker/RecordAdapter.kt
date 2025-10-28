package com.example.notetaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notetaker.databinding.ItemRecordBinding

class RecordAdapter(
    private val onRecordClick: (Record) -> Unit
) : ListAdapter<Record, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(
        private val binding: ItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: Record) {
            binding.tvName.text = record.name
            binding.tvDate.text = record.date
            binding.tvNumber.text = record.number

            // Load the first image if available
            val imagePaths = record.imagePaths.split(",").filter { it.isNotEmpty() }
            if (imagePaths.isNotEmpty()) {
                Glide.with(binding.ivThumbnail.context)
                    .load(imagePaths[0])
                    .placeholder(R.drawable.ic_camera_placeholder)
                    .centerCrop()
                    .into(binding.ivThumbnail)
            } else {
                binding.ivThumbnail.setImageResource(R.drawable.ic_camera_placeholder)
            }

            binding.root.setOnClickListener {
                onRecordClick(record)
            }
        }
    }

    class RecordDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }
}