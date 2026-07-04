package com.example.data

import android.content.Context
import com.example.security.SecurityUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class FileVaultManager(private val context: Context) {
    private val vaultDir: File by lazy {
        File(context.filesDir, "vault_storage").apply { if (!exists()) mkdirs() }
    }

    fun encryptAndStore(originalFile: File, type: String): VaultItem? {
        return try {
            val encryptedFileName = "${UUID.randomUUID()}.${originalFile.extension}"
            val encryptedFile = File(vaultDir, encryptedFileName)

            val bytes = originalFile.readBytes()
            val encryptedBytes = SecurityUtils.encryptData(bytes)
            encryptedFile.writeBytes(encryptedBytes)

            // CRITICAL: Delete original file after successful encryption
            if (originalFile.exists()) {
                originalFile.delete()
            }

            VaultItem(
                name = originalFile.name,
                originalPath = originalFile.absolutePath,
                encryptedPath = encryptedFile.absolutePath,
                type = type,
                size = originalFile.length()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decryptAndRestore(item: VaultItem): Boolean {
        return try {
            val encryptedFile = File(item.encryptedPath)
            if (!encryptedFile.exists()) return false
            
            val encryptedBytes = encryptedFile.readBytes()
            val decryptedBytes = SecurityUtils.decryptData(encryptedBytes)

            val restoreFile = File(item.originalPath)
            // Ensure parent directory exists
            restoreFile.parentFile?.mkdirs()
            restoreFile.writeBytes(decryptedBytes)
            
            // Delete the encrypted file from internal storage
            encryptedFile.delete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun decryptAndRetrieve(item: VaultItem): File? {
        return try {
            val encryptedFile = File(item.encryptedPath)
            val encryptedBytes = encryptedFile.readBytes()
            val decryptedBytes = SecurityUtils.decryptData(encryptedBytes)

            val tempFile = File(context.cacheDir, item.name)
            tempFile.writeBytes(decryptedBytes)
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
