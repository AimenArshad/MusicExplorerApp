package com.example.musicexplorer.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicexplorer.R
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.data.AudioFileDatabase
import com.example.musicexplorer.data.AudioFileRepository
import com.example.musicexplorer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding        //deferred initializations
    private lateinit var audioAdapter: AudioFileAdapter
    private lateinit var viewModel: AudioFileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Dao = AudioFileDatabase.getDatabase(applicationContext).audioFileDao()
        val Repository = AudioFileRepository(Dao)
        viewModel =
            ViewModelProvider(this, AudioViewModelFactory(Repository))[AudioFileViewModel::class.java]

        setupRecyclerView()
        checkPermissionStatus()
        setupSearchListener()
    }

    private fun checkPermissionStatus() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show()
            viewModel.scanAndStoreAudioFiles(this)
            observeAudioList()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show()
            viewModel.scanAndStoreAudioFiles(this)
            observeAudioList()
        } else {
            launchEmptyActivity("Permission to Access Audio Files Denied")
        }
    }


    private fun setupRecyclerView() {
        audioAdapter = AudioFileAdapter { audio ->
            openDetailFragment(audio)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = audioAdapter
        }
    }
    private fun openDetailFragment(audio: AudioFile) {
        binding.fragmentView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        val bundle = Bundle().apply {
            putParcelable("audioFile", audio)
        }

        val fragment = AudioDetailFragment().apply {
            arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun setupSearchListener() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSearchQuery(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    private fun observeAudioList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredAudioFiles.collect { list ->
                    if (list.isNotEmpty()) {
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        audioAdapter.submitList(list)
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    }
                    }

                }
            }
        }


    private fun launchEmptyActivity(message: String) {
            val intent = Intent(this, EmptyStateActivity::class.java)
            intent.putExtra("infoMessage", message)
            startActivity(intent)
            finish()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            binding.fragmentView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
