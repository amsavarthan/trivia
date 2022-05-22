package com.amsavarthan.game.trivia.di

import android.content.Context
import com.amsavarthan.game.trivia.data.preference.GameDatastore
import com.amsavarthan.game.trivia.data.preference.TokenDatastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatastoreModule {

    @Provides
    @Singleton
    fun providesTokenDatastore(@ApplicationContext context: Context) = TokenDatastore(context)

    @Provides
    @Singleton
    fun providesGameDatastore(@ApplicationContext context: Context) = GameDatastore(context)

}