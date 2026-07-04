package com.example.ui.vault

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)
    
    private val _preventScreenshots = MutableStateFlow(prefs.getBoolean("prevent_screenshots", false))
    val preventScreenshots: StateFlow<Boolean> = _preventScreenshots.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString("language", "English") ?: "English")
    val language: StateFlow<String> = _language.asStateFlow()

    fun togglePreventScreenshots(enabled: Boolean) {
        _preventScreenshots.value = enabled
        prefs.edit().putBoolean("prevent_screenshots", enabled).apply()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("language", lang).apply()
        
        val appLocale: LocaleListCompat = if (lang == "Hindi") {
            LocaleListCompat.forLanguageTags("hi")
        } else {
            LocaleListCompat.forLanguageTags("en")
        }
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun shareApp() {
        val application = getApplication<Application>()
        val context = application.applicationContext
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val apkFile = java.io.File(context.packageCodePath)
                val cacheFile = java.io.File(context.cacheDir, "CalculatorVault.apk")
                
                // Copy APK to cache for sharing
                apkFile.inputStream().use { input ->
                    cacheFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val apkUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    cacheFile
                )

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out Calculator Vault: A premium calculator with a hidden secure vault for your photos and files!")
                    putExtra(Intent.EXTRA_STREAM, apkUri)
                    type = "application/vnd.android.package-archive"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                val chooser = Intent.createChooser(shareIntent, "Share App APK")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to text sharing if APK sharing fails
                val textIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out Calculator Vault: A premium calculator with a hidden secure vault!")
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(textIntent, "Share App")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        }
    }
}
