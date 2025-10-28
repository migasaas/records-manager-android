package com.example.notetaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notetaker.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImageSlider()
        setupObservers()
        setupClickListeners()

        val recordId = intent.getLongExtra("record_id", -1)
        if (recordId != -1L) {
            viewModel.loadRecord(recordId)
        } else {
            finish() // Close if no valid ID is passed
        }
    }

    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(emptyList())
        binding.viewPagerImages.adapter = imageSliderAdapter

        binding.viewPagerImages.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageIndicator(position)
            }
        })
    }

    private fun setupObservers() {
        viewModel.record.observe(this) { record ->
            record?.let {
                binding.tvName.text = it.name
                binding.tvNumber.text = "الرقم: ${it.number}"
                binding.tvDate.text = "التاريخ: ${it.date}"
                binding.tvAction.text = it.action

                val imageUris = it.imagePaths.split(",")
                    .filter { path -> path.isNotEmpty() }
                    .map { Uri.parse(it) }

                if (imageUris.isNotEmpty()) {
                    imageSliderAdapter.updateImages(imageUris)
                    binding.viewPagerImages.visibility = android.view.View.VISIBLE
                    binding.tvImageIndicator.visibility = android.view.View.VISIBLE
                    updateImageIndicator(0)
                } else {
                    binding.viewPagerImages.visibility = android.view.View.GONE
                    binding.tvImageIndicator.visibility = android.view.View.GONE
                }
            }
        }

        viewModel.deleteStatus.observe(this) { isDeleted ->
            if (isDeleted) {
                Toast.makeText(this, getString(R.string.msg_record_deleted), Toast.LENGTH_SHORT).show()
                finish()
                viewModel.onDeleteComplete()
            }
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.btnEdit.setOnClickListener {
            val recordId = viewModel.record.value?.id ?: -1
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("record_id", recordId)
            startActivity(intent)
            finish()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun updateImageIndicator(position: Int) {
        val totalItems = imageSliderAdapter.itemCount
        binding.tvImageIndicator.text = "${position + 1} / $totalItems"
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this record? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteRecord()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
