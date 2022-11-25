package com.amsavarthan.game.trivia.di

import android.content.Context
import com.amsavarthan.game.trivia.config.API_BASE_URL
import com.amsavarthan.game.trivia.config.TRIVIA_BASE_URL
import com.amsavarthan.game.trivia.data.api.GameAPI
import com.amsavarthan.game.trivia.data.api.QuestionsAPI
import com.amsavarthan.game.trivia.data.api.RoomAPI
import com.amsavarthan.game.trivia.data.api.interceptor.NetworkInterceptor
import com.amsavarthan.game.trivia.data.api.interceptor.TokenInterceptor
import com.amsavarthan.game.trivia.data.preference.TokenPreferencesRepository
import com.amsavarthan.game.trivia.data.repository.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenTriviaAPI

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MyAPI


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesNetworkInterceptor(@ApplicationContext context: Context): NetworkInterceptor {
        return NetworkInterceptor(context)
    }

    @Singleton
    @Provides
    fun providesTokenInterceptor(
        tokenPreferencesRepository: TokenPreferencesRepository,
        moshi: Moshi
    ): TokenInterceptor {
        return TokenInterceptor(tokenPreferencesRepository, moshi)
    }

    @Singleton
    @Provides
    @OpenTriviaAPI
    fun providesOkHttpClientForOpenTriviaApi(
        networkInterceptor: NetworkInterceptor,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @MyAPI
    fun providesOkHttpClientForMyApi(
        networkInterceptor: NetworkInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(networkInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    fun providesMoshiFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    @OpenTriviaAPI
    fun providesRetrofitForOpenTriviaApi(
        @OpenTriviaAPI okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .baseUrl(TRIVIA_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @MyAPI
    fun providesRetrofitForMyApi(
        @MyAPI okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .baseUrl(API_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesQuestionsApiService(@OpenTriviaAPI retrofit: Retrofit): QuestionsAPI {
        return retrofit.create(QuestionsAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesQuestionsRepository(questionsAPI: QuestionsAPI): QuestionsRepository {
        return QuestionsRepositoryImpl(questionsAPI)
    }

    @Singleton
    @Provides
    fun providesRoomsApiService(@MyAPI retrofit: Retrofit): RoomAPI {
        return retrofit.create(RoomAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesRoomRepository(roomAPI: RoomAPI): RoomRepository {
        return RoomRepositoryImpl(roomAPI)
    }

    @Singleton
    @Provides
    fun providesGameApiService(@MyAPI retrofit: Retrofit): GameAPI {
        return retrofit.create(GameAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesGameRepository(gameAPI: GameAPI): GameRepository {
        return GameRepositoryImpl(gameAPI)
    }

}