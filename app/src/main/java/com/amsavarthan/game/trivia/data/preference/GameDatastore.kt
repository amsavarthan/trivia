package com.amsavarthan.game.trivia.data.preference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.amsavarthan.game.trivia.GamePreferences
import com.amsavarthan.game.trivia.config.Config.MAX_ENERGY
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object GamePreferencesSerializer : Serializer<GamePreferences> {
    override val defaultValue: GamePreferences
        get() = GamePreferences.getDefaultInstance().toBuilder().setEnergy(MAX_ENERGY).build()

    override suspend fun readFrom(input: InputStream): GamePreferences {
        try {
            return GamePreferences.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: GamePreferences, output: OutputStream) {
        t.writeTo(output)
    }

}

interface GamePreferencesRepository : PreferencesRepository<GamePreferences> {
    suspend fun decreaseEnergy()
    suspend fun increaseEnergy()
    suspend fun increaseGamePlayCount()
}

class GamePreferencesRepositoryImpl(
    private val gamePreferencesStore: DataStore<GamePreferences>
) : GamePreferencesRepository {

    override val preferencesFlow: Flow<GamePreferences>
        get() = gamePreferencesStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(GamePreferencesSerializer.defaultValue)
                } else {
                    throw exception
                }
            }

    override suspend fun fetchInitialPreferences(): GamePreferences {
        return gamePreferencesStore.data.first()
    }

    override suspend fun decreaseEnergy() {
        gamePreferencesStore.updateData { prefs ->
            var energy = prefs.energy - 1
            if (energy < 0) energy = 0
            prefs.toBuilder().setEnergy(energy).build()
        }
    }

    override suspend fun increaseEnergy() {
        gamePreferencesStore.updateData { prefs ->
            var energy = prefs.energy + 1
            if (energy > MAX_ENERGY) energy = MAX_ENERGY
            prefs.toBuilder().setEnergy(energy).build()
        }
    }

    override suspend fun increaseGamePlayCount() {
        gamePreferencesStore.updateData { prefs ->
            val gamesPlayed = prefs.gamesPlayed + 1
            prefs.toBuilder().setGamesPlayed(gamesPlayed).build()
        }
    }

}