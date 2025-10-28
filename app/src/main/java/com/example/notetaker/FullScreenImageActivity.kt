package com.example.notetaker

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notetaker.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getParcelableExtra<Uri>("image_uri")
        val transitionName = intent.getStringExtra("transition_name")

        binding.ivFullScreen.transitionName = transitionName

        Glide.with(this)
            .load(imageUri)
            .into(binding.ivFullScreen)

        binding.ivFullScreen.setOnClickListener {
            supportFinishAfterTransition()
        }
    }
}