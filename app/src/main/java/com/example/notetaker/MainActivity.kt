package com.example.notetaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recordAdapter: RecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        recordAdapter = RecordAdapter { record ->
            openDetailActivity(record)
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = recordAdapter
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(this) { records ->
            if (records.isNullOrEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                recordAdapter.submitList(records)
            }
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddRecord.setOnClickListener {
            openAddRecordActivity()
        }
    }

    private fun showEmptyState() {
        binding.rvSearchResults.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding.rvSearchResults.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
    }

    private fun openDetailActivity(record: Record) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("record_id", record.id)
        }
        startActivity(intent)
    }

    private fun openAddRecordActivity() {
        val intent = Intent(this, AddRecordActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        // Expand the search view by default
        searchItem.expandActionView()
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchRecords(newText.orEmpty())
                return true
            }
        })
        
        return true
    }
}
