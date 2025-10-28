package com.example.notetaker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSliderAdapter(private var imageUris: List<Uri>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    fun updateImages(newUris: List<Uri>) {
        imageUris = newUris
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_slider, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int = imageUris.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivSliderImage)

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .placeholder(R.drawable.ic_camera_placeholder)
                .into(imageView)
        }
    }
}