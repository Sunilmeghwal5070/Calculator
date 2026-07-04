package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.VaultDao
import com.example.data.VaultRepository
import com.example.data.FileVaultManager
import com.example.ui.calculator.CalculatorViewModel
import com.example.ui.setup.SetupViewModel
import android.app.Application
import com.example.ui.vault.SettingsViewModel
import com.example.ui.vault.VaultViewModel

class ViewModelFactory(
    private val repository: VaultRepository,
    private val vaultDao: VaultDao,
    private val fileVaultManager: FileVaultManager,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CalculatorViewModel::class.java) -> {
                CalculatorViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SetupViewModel::class.java) -> {
                SetupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VaultViewModel::class.java) -> {
                VaultViewModel(vaultDao, fileVaultManager) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
