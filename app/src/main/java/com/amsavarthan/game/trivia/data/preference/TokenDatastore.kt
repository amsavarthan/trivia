package com.amsavarthan.game.trivia.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val TOKEN_PREFERENCES_NAME = "token_preferences"
private val Context.datastore by preferencesDataStore(name = TOKEN_PREFERENCES_NAME)

class TokenDatastore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.datastore
    private val TOKEN = stringPreferencesKey("token")

    val preferencesFlow: Flow<String> = dataStore.data.catch {
        if (it !is IOException) throw it
        it.printStackTrace()
        emit(emptyPreferences())
    }.map { preferences -> preferences[TOKEN] ?: "" }

    suspend fun saveTokenToPreferencesStore(token: String) {
        dataStore.edit { preferences -> preferences[TOKEN] = token }
    }

}