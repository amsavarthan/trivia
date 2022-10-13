package com.amsavarthan.game.trivia.data.preference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.amsavarthan.game.trivia.TokenPreferences
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object TokenPreferencesSerializer : Serializer<TokenPreferences> {
    override val defaultValue: TokenPreferences
        get() = TokenPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TokenPreferences {
        try {
            return TokenPreferences.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: TokenPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

interface TokenPreferencesRepository : PreferencesRepository<TokenPreferences> {
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}

class TokenPreferencesRepositoryImpl(
    private val tokenPreferencesStore: DataStore<TokenPreferences>
) : TokenPreferencesRepository {

    override val preferencesFlow: Flow<TokenPreferences>
        get() = tokenPreferencesStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(TokenPreferencesSerializer.defaultValue)
                } else {
                    throw exception
                }
            }

    override suspend fun fetchInitialPreferences(): TokenPreferences {
        return tokenPreferencesStore.data.first()
    }

    override suspend fun saveToken(token: String) {
        tokenPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setSessionToken(token).build()
        }
    }

    override suspend fun clearToken() {
        tokenPreferencesStore.updateData { prefs ->
            prefs.toBuilder().clearSessionToken().build()
        }
    }

}