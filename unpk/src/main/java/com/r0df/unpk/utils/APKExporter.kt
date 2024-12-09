package com.r0df.unpk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileInputStream

object APKExporter {

    private const val REQUEST_CODE_OPEN_DIRECTORY = 1001
    private lateinit var apkFiles: List<File>
    private lateinit var appName: String

    fun exportAPK(activity: Activity, appInfo: PackageHelper.AppInfo, rootView: View) {
        try {
            apkFiles = PackageHelper.getSplitAPKFiles(activity, appInfo.packageName)
            appName = appInfo.label

            if (apkFiles.isEmpty()) {
                throw IllegalStateException("any APK file was found inside the directories.")
            }


            promptForFolderSelection(activity)
        } catch (e: Exception) {
            Snackbar.make(rootView, "FAILED export APK: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun promptForFolderSelection(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }

    fun handleActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?, rootView: View) {
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->

                saveAPKsToUri(context, apkFiles, appName, uri, rootView)
            } ?: run {
                Snackbar.make(rootView, "no directories were selected.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun saveAPKsToUri(context: Context, files: List<File>, appName: String, directoryUri: Uri, rootView: View) {
        try {
            val resolver = context.contentResolver

            val docUri = DocumentsContract.buildDocumentUriUsingTree(
                directoryUri,
                DocumentsContract.getTreeDocumentId(directoryUri)
            )

            files.forEach { file ->
                val fileName = when {
                    file.name == "base.apk" -> "${appName}_base.apk"
                    file.name.startsWith("split_config") -> {
                        val splitConfig = file.name.substringAfter("split_config.").substringBefore(".apk")
                        "${appName}_split_${splitConfig}.apk"
                    }
                    else -> "${appName}_${file.name}"
                }

                val newFileUri = DocumentsContract.createDocument(
                    resolver,
                    docUri,
                    "application/vnd.android.package-archive",
                    fileName
                )

                if (newFileUri != null) {
                    resolver.openOutputStream(newFileUri).use { outputStream ->
                        FileInputStream(file).use { inputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                } else {
                    throw Exception("Not possible to create file: $fileName")
                }
            }

            Snackbar.make(rootView, "APKs exported with success", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(rootView, "FAILED save apks: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }
}