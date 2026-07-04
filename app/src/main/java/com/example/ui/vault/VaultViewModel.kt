package com.example.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VaultDao
import com.example.data.VaultItem
import com.example.data.FileVaultManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VaultViewModel(
    private val vaultDao: VaultDao,
    private val fileVaultManager: FileVaultManager
) : ViewModel() {
    private val _items = MutableStateFlow<List<VaultItem>>(emptyList())
    val items: StateFlow<List<VaultItem>> = _items.asStateFlow()

    private val _categoryCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val categoryCounts: StateFlow<Map<String, Int>> = _categoryCounts.asStateFlow()

    init {
        loadCounts()
    }

    private fun loadCounts() {
        viewModelScope.launch {
            vaultDao.getAllItems().collect { allItems ->
                val counts = allItems.groupBy { it.type }.mapValues { it.value.size }
                _categoryCounts.value = counts
            }
        }
    }

    fun loadItems(type: String) {
        viewModelScope.launch {
            vaultDao.getItemsByType(type).collect {
                _items.value = it
            }
        }
    }

    fun addItem(file: java.io.File, type: String, originalUri: android.net.Uri? = null, context: android.content.Context? = null) {
        viewModelScope.launch {
            val vaultItem = fileVaultManager.encryptAndStore(file, type)
            if (vaultItem != null) {
                vaultDao.insertItem(vaultItem)
                
                // If we have an original URI and context, delete it from MediaStore
                if (originalUri != null && context != null) {
                    try {
                        context.contentResolver.delete(originalUri, null, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                // Also try to delete the temporary file if it exists
                if (file.exists() && file.absolutePath.contains("cache")) {
                    file.delete()
                }
            }
        }
    }

    fun unhideItem(item: VaultItem) {
        viewModelScope.launch {
            val success = fileVaultManager.decryptAndRestore(item)
            if (success) {
                vaultDao.deleteItem(item)
            }
        }
    }
}
