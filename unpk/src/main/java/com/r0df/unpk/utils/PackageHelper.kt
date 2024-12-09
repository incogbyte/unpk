package com.r0df.unpk.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileNotFoundException

object PackageHelper {

    data class AppInfo(
        val label: String,
        val packageName: String,
        val path: String,
        val icon: Drawable,
        val isSplitAPK: Boolean
    )


    fun getInstalledApps(context: Context, includeSystemApps: Boolean = false): List<AppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppInfo>()

        for (packageInfo in packages) {
            try {

                if (!includeSystemApps && (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                    continue
                }

                val label = packageInfo.loadLabel(pm).toString()
                val icon = packageInfo.loadIcon(pm)
                val path = packageInfo.sourceDir
                val isSplitAPK = isSplitAPK(context, packageInfo.packageName)

                appList.add(AppInfo(label, packageInfo.packageName, path, icon, isSplitAPK))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return appList
    }

    fun isSplitAPK(context: Context, packageName: String): Boolean {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            val splitDirs = appInfo.splitSourceDirs
            splitDirs != null && splitDirs.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun getSplitAPKFiles(context: Context, packageName: String): List<File> {
        val pm = context.packageManager
        val files = mutableListOf<File>()

        try {
            val appInfo = pm.getApplicationInfo(packageName, 0)


            val baseAPK = File(appInfo.sourceDir)
            if (baseAPK.exists()) {
                files.add(baseAPK)
            } else {
                throw FileNotFoundException("Base APK not found: ${appInfo.sourceDir}")
            }


            appInfo.splitSourceDirs?.forEach { splitPath ->
                val splitFile = File(splitPath)
                if (splitFile.exists()) {
                    files.add(splitFile)
                } else {
                    throw FileNotFoundException("Split APK not found: $splitPath")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Error while get the APK $packageName: ${e.message}")
        }

        return files
    }
}