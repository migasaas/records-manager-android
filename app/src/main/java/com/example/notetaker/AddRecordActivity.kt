package com.example.notetaker

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.notetaker.databinding.ActivityAddRecordBinding
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecordBinding
    private val viewModel: MainViewModel by viewModels()

    private var currentImageUris: MutableList<Uri> = mutableListOf()
    private var currentImageIndex: Int = 0
    private var tempImageUri: Uri? = null
    private lateinit var thumbnailAdapter: ThumbnailAdapter

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, getString(R.string.msg_camera_permission_required), Toast.LENGTH_LONG).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            tempImageUri?.let { uri ->
                currentImageUris.add(uri)
                currentImageIndex = currentImageUris.size - 1
                displayImages()
                updateNavigation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupClickListeners()
        setCurrentDate()
    }

    private fun setupUI() {
        setupToolbar()
        setupThumbnailRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.title_add_record)
    }

    private fun setupThumbnailRecyclerView() {
        thumbnailAdapter = ThumbnailAdapter(currentImageUris, ::selectImage, currentImageIndex)
        binding.rvThumbnails.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvThumbnails.adapter = thumbnailAdapter
    }

    private fun setupObservers() {
        viewModel.saveStatus.observe(this) { isSaved ->
            if (isSaved) {
                Toast.makeText(this, getString(R.string.msg_record_saved), Toast.LENGTH_SHORT).show()
                finish()
                viewModel.onSaveComplete()
            }
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddImage.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.btnSave.setOnClickListener {
            saveRecord()
        }

        binding.btnPrevious.setOnClickListener {
            previousImage()
        }

        binding.btnNext.setOnClickListener {
            nextImage()
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setCurrentDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        binding.etDate.setText(currentDate)
    }

    private fun checkCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        tempImageUri = createImageUri()
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        cameraLauncher.launch(cameraIntent)
    }

    private fun createImageUri(): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timestamp}_"
        val storageDir = File(filesDir, "images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        return FileProvider.getUriForFile(
            this,
            "com.example.notetaker.fileprovider",
            imageFile
        )
    }

    private fun displayImages() {
        if (currentImageUris.isEmpty()) {
            binding.ivMainImage.setImageResource(R.drawable.ic_camera_placeholder)
            binding.tvImageCounter.text = ""
            binding.tvCurrentImage.text = ""
        } else {
            val currentUri = currentImageUris[currentImageIndex]
            Glide.with(this)
                .load(currentUri)
                .placeholder(R.drawable.ic_camera_placeholder)
                .into(binding.ivMainImage)

            binding.ivMainImage.transitionName = "image_transition_${currentImageIndex}"

            binding.ivMainImage.setOnClickListener {
                val intent = Intent(this, FullScreenImageActivity::class.java).apply {
                    putExtra("image_uri", currentUri)
                    putExtra("transition_name", binding.ivMainImage.transitionName)
                }
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    binding.ivMainImage,
                    binding.ivMainImage.transitionName
                )
                startActivity(intent, options.toBundle())
            }

            binding.tvImageCounter.text = "${currentImageIndex + 1}/${currentImageUris.size}"
            binding.tvCurrentImage.text = "الصورة ${currentImageIndex + 1} من ${currentImageUris.size}"
        }
        thumbnailAdapter.updateImages(currentImageUris, currentImageIndex)
    }

    private fun selectImage(index: Int) {
        if (index < currentImageUris.size) {
            currentImageIndex = index
            displayImages()
            updateNavigation()
        }
    }

    private fun previousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--
            displayImages()
            updateNavigation()
        }
    }

    private fun nextImage() {
        if (currentImageIndex < currentImageUris.size - 1) {
            currentImageIndex++
            displayImages()
            updateNavigation()
        }
    }

    private fun updateNavigation() {
        binding.btnPrevious.isEnabled = currentImageIndex > 0
        binding.btnNext.isEnabled = currentImageIndex < currentImageUris.size - 1
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etDate.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun saveRecord() {
        val name = binding.etName.text.toString().trim()
        val number = binding.etNumber.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val action = binding.etAction.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_name_required), Toast.LENGTH_SHORT).show()
            return
        }

        if (number.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_number_required), Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrisString = currentImageUris.joinToString(",") { it.toString() }
        viewModel.saveRecord(name, number, date, action, imageUrisString)
    }
}