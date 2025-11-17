package com.example.musicexplorer.view

import InAppMessageDisplay
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.data.AudioFileDatabase
import com.example.musicexplorer.data.AudioFileRepository
import com.example.musicexplorer.databinding.ActivityMainBinding
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var audioAdapter: AudioFileAdapter
    private lateinit var viewModel: AudioFileViewModel
    private lateinit var firebaseInAppMessaging: FirebaseInAppMessaging
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFirebase()

        val forceRefresh = true
        FirebaseInstallations.getInstance().getToken(forceRefresh)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation auth token: " + task.result?.token)
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AudioFileDatabase.getDatabase(applicationContext).audioFileDao()
        val repository = AudioFileRepository(dao)
        viewModel = ViewModelProvider(this, AudioViewModelFactory(repository))[AudioFileViewModel::class.java]
        setupRecyclerView()
        checkPermissionStatus()
        setupSearchListener()
        firebaseInAppMessaging.triggerEvent("app_opened")

    }

    private fun setupFirebase() {
        firebaseInAppMessaging = FirebaseInAppMessaging.getInstance()
        firebaseInAppMessaging.setMessageDisplayComponent (InAppMessageDisplay(this))
        Log.d("FIAM", "Custom InAppMessageDisplay registered.")
        val cacheFile = File(filesDir, "fiam_eligible_campaigns_cache_file")
        if (cacheFile.exists()) {
            cacheFile.delete()
            Log.d("FIAM", "Cleared FIAM cache for fresh fetch")
        }


    }
    private fun checkPermissionStatus() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.scanAndStoreAudioFiles(this)
            observeAudioList()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanAndStoreAudioFiles(this)
            observeAudioList()
        } else {
            launchEmptyActivity("Permission to Access Audio Files Denied")
        }
    }

    private fun setupRecyclerView() {
        audioAdapter = AudioFileAdapter { audio -> openDetailFragment(audio) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = audioAdapter
        }
    }

    private fun openDetailFragment(audio: AudioFile) {
        binding.fragmentView.visibility = android.view.View.VISIBLE
        binding.recyclerView.visibility = android.view.View.GONE
       // firebaseInAppMessaging.triggerEvent("details_opened")
        firebaseInAppMessaging.triggerEvent("opened_trigger")


        val bundle = Bundle().apply { putParcelable("audioFile", audio) }
        val fragment = AudioDetailFragment().apply { arguments = bundle }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentView.id, fragment)
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
                    binding.recyclerView.visibility = if (list.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                    binding.emptyView.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                    audioAdapter.submitList(list)
                }
            }
        }
    }

    private fun launchEmptyActivity(message: String) {
        startActivity(Intent(this, EmptyStateActivity::class.java).apply {
            putExtra("infoMessage", message)
        })
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            binding.fragmentView.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE

            firebaseInAppMessaging.triggerEvent("test_exp")
        } else {
            super.onBackPressed()
        }
    }
}
