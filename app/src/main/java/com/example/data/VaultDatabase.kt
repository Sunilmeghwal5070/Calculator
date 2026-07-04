package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items WHERE isDeleted = 0 AND type = :type ORDER BY timestamp DESC")
    fun getItemsByType(type: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE isDeleted = 0")
    fun getAllItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE isFavorite = 1 AND isDeleted = 0")
    fun getFavorites(): Flow<List<VaultItem>>

    @Insert
    suspend fun insertItem(item: VaultItem)

    @Update
    suspend fun updateItem(item: VaultItem)

    @Delete
    suspend fun deleteItem(item: VaultItem)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}

@Database(entities = [VaultItem::class, Note::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao
    abstract fun noteDao(): NoteDao
}
