package com.amsavarthan.game.trivia.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.amsavarthan.game.trivia.GamePreferences
import com.amsavarthan.game.trivia.TokenPreferences
import com.amsavarthan.game.trivia.data.preference.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TOKEN_DATA_STORE_FILE_NAME = "token_prefs.proto"

@Module
@InstallIn(SingletonComponent::class)
class DatastoreModule {

    @Provides
    @Singleton
    fun providesTokenDatastore(@ApplicationContext context: Context): DataStore<TokenPreferences> {
        return DataStoreFactory.create(
            serializer = TokenPreferencesSerializer,
            produceFile = {
                context.dataStoreFile(TOKEN_DATA_STORE_FILE_NAME)
            }
        )
    }

    @Provides
    @Singleton
    fun providesTokenDatastoreRepository(
        dataStore: DataStore<TokenPreferences>
    ): TokenPreferencesRepository {
        return TokenPreferencesRepositoryImpl(dataStore)
    }

}