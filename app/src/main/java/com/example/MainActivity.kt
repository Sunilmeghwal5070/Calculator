package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.VaultDatabase
import com.example.data.VaultRepository
import com.example.navigation.NavGraph
import com.example.ui.calculator.CalculatorViewModel
import com.example.ui.setup.SetupViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.vault.VaultViewModel
import com.example.ui.vault.SettingsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var database: VaultDatabase
    private lateinit var repository: VaultRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = Room.databaseBuilder(
            applicationContext,
            VaultDatabase::class.java, "vault-database"
        ).build()
        
        repository = VaultRepository(applicationContext)
        val fileVaultManager = com.example.data.FileVaultManager(applicationContext)
        
        val factory = ViewModelFactory(repository, database.vaultDao(), fileVaultManager, application)
        val calculatorViewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]
        val setupViewModel = ViewModelProvider(this, factory)[SetupViewModel::class.java]
        val vaultViewModel = ViewModelProvider(this, factory)[VaultViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        // Observe lifecycle to reset state when app goes to background
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                // When app is minimized, reset the vault access state
                calculatorViewModel.resetUnlock()
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)

        enableEdgeToEdge()
        setContent {
            val preventScreenshots by settingsViewModel.preventScreenshots.collectAsState()
            var currentRoute by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(preventScreenshots, currentRoute) {
                // Prevent screenshots if setting is enabled AND we are NOT on the calculator screen
                val isCalculator = currentRoute?.contains("CalculatorRoute") == true
                
                // Note: FLAG_SECURE causes black screen in streaming previews.
                // We apply it only if preventScreenshots is true and we're not on calculator.
                if (preventScreenshots && !isCalculator) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            MyApplicationTheme {
                NavGraph(
                    vaultRepository = repository,
                    calculatorViewModel = calculatorViewModel,
                    setupViewModel = setupViewModel,
                    vaultViewModel = vaultViewModel,
                    settingsViewModel = settingsViewModel,
                    onRouteChanged = { currentRoute = it }
                )
            }
        }
    }
}
