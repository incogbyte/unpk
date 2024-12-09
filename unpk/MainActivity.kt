package drawable

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.r0df.unpk.R
import com.r0df.unpk.utils.APKExporter
import com.r0df.unpk.utils.PackageHelper

class MainActivity : AppCompatActivity() {

    // Lateinit properties for UI components
    private lateinit var listView: ListView
    private lateinit var exportButton: Button
    private var selectedApp: PackageHelper.AppInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        // Initialize UI components
        listView = findViewById(R.id.app_list)
        exportButton = findViewById(R.id.export_button)

        // Load installed apps into the list view
        setupAppListView()

        // Handle the export button click
        setupExportButton()
    }

    private fun setupAppListView() {
        try {
            val apps = PackageHelper.getInstalledApps(this)
            if (apps.isEmpty()) {
                Toast.makeText(this, "No installed apps found.", Toast.LENGTH_LONG).show()
                return
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, apps.map { it.label })
            listView.adapter = adapter

            // Set item click listener for selecting an app
            listView.setOnItemClickListener { _, _, position, _ ->
                selectedApp = apps[position]
                Toast.makeText(this, "Selected: ${selectedApp?.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load apps: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupExportButton() {
        exportButton.setOnClickListener {
            selectedApp?.let {
                try {
                    APKExporter.exportAPK(this, it)
                } catch (e: Exception) {
                    Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(this, "Please select an app first!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}