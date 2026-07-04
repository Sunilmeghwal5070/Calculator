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

    private val _currentAlias = MutableStateFlow(prefs.getString("current_alias", "com.example.CalculatorAlias") ?: "com.example.CalculatorAlias")
    val currentAlias: StateFlow<String> = _currentAlias.asStateFlow()

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

    fun updateIcon(newAlias: String) {
        val context = getApplication<Application>()
        val pm = context.packageManager
        
        val aliases = listOf(
            "com.example.CalculatorAlias",
            "com.example.WeatherAlias",
            "com.example.ClockAlias",
            "com.example.NoteAlias",
            "com.example.BrowserAlias",
            "com.example.CameraAlias",
            "com.example.MusicAlias",
            "com.example.GalleryAlias",
            "com.example.FilesAlias",
            "com.example.SettingsAlias",
            "com.example.ContactsAlias",
            "com.example.MessagesAlias",
            "com.example.RadioAlias"
        )

        // 1. Enable the new one first
        try {
            pm.setComponentEnabledSetting(
                ComponentName(context, newAlias),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Disable others
        aliases.filter { it != newAlias }.forEach { alias ->
            try {
                pm.setComponentEnabledSetting(
                    ComponentName(context, alias),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        _currentAlias.value = newAlias
        prefs.edit().putString("current_alias", newAlias).apply()
        
        // Note: The app will likely be killed by the system here as the launcher component changed.
        // This is expected Android behavior.
    }

    fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this secure Calculator Vault app!")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(shareIntent)
    }
}
