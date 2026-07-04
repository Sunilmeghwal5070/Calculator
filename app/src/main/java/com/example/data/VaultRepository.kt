package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "vault_settings")

class VaultRepository(private val context: Context) {
    companion object {
        private val PIN_KEY = stringPreferencesKey("vault_pin")
        private val SECURITY_QUESTION_KEY = stringPreferencesKey("security_question")
        private val SECURITY_ANSWER_KEY = stringPreferencesKey("security_answer")
        private val IS_SETUP_COMPLETE = stringPreferencesKey("is_setup_complete")
    }

    val pinFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PIN_KEY]
    }

    val isSetupComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SETUP_COMPLETE] == "true"
    }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }

    suspend fun completeSetup() {
        context.dataStore.edit { preferences ->
            preferences[IS_SETUP_COMPLETE] = "true"
        }
    }

    suspend fun saveSecurityInfo(question: String, answer: String) {
        context.dataStore.edit { preferences ->
            preferences[SECURITY_QUESTION_KEY] = question
            preferences[SECURITY_ANSWER_KEY] = answer
        }
    }
}
