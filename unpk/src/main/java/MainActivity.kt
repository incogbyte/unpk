package com.r0df.unpk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.r0df.unpk.adapters.AppListAdapter
import com.r0df.unpk.utils.APKExporter
import com.r0df.unpk.utils.PackageHelper

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var searchBar: EditText
    private lateinit var listView: ListView
    private lateinit var exportButton: Button
    private lateinit var titleView: TextView

    // Data and adapter
    private var apps = listOf<PackageHelper.AppInfo>()
    private lateinit var adapter: AppListAdapter
    private var selectedApps = mutableSetOf<PackageHelper.AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initializeUI()
            setupSearchBar()
            loadApps()
            setupExportButton()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error during initialization: ${e.message}", e)
            Toast.makeText(this, "Initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeUI() {
        try {
            searchBar = findViewById(R.id.search_bar) ?: throw IllegalStateException("searchBar view not found")
            listView = findViewById(R.id.app_list) ?: throw IllegalStateException("listView view not found")
            exportButton = findViewById(R.id.export_button) ?: throw IllegalStateException("exportButton view not found")
            titleView = findViewById(R.id.title) ?: throw IllegalStateException("titleView view not found")

            Log.d("MainActivity", "UI components initialized successfully.")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing UI components: ${e.message}", e)
            throw e
        }
    }

    private fun loadApps() {
        try {
            apps = PackageHelper.getInstalledApps(this)
            if (apps.isEmpty()) {
                Toast.makeText(this, "No installed apps found.", Toast.LENGTH_LONG).show()
                return
            }

            // Set up the adapter and attach to the ListView
            adapter = AppListAdapter(this, apps, selectedApps)
            listView.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading apps: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSearchBar() {
        if (!::searchBar.isInitialized) {
            throw IllegalStateException("searchBar not initialized!")
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredApps = apps.filter { it.label.contains(s ?: "", ignoreCase = true) }
                adapter.updateList(filteredApps)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupExportButton() {
        exportButton.setOnClickListener {
            if (selectedApps.isNotEmpty()) {
                try {

                    val rootView: View = findViewById(android.R.id.content)
                    selectedApps.forEach { app ->
                        APKExporter.exportAPK(this, app, rootView)
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(exportButton, "Please select at least one app!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_instructions -> {

                val intent = Intent(this, CommandsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val rootView: View = findViewById(android.R.id.content)
        APKExporter.handleActivityResult(this, requestCode, resultCode, data, rootView)
    }
}