package com.amsavarthan.game.trivia.di

import android.content.Context
import com.amsavarthan.game.trivia.config.BASE_URL
import com.amsavarthan.game.trivia.data.api.QuestionsAPI
import com.amsavarthan.game.trivia.data.api.interceptor.NetworkInterceptor
import com.amsavarthan.game.trivia.data.api.interceptor.TokenInterceptor
import com.amsavarthan.game.trivia.data.preference.TokenPreferencesRepository
import com.amsavarthan.game.trivia.data.repository.QuestionsRepository
import com.amsavarthan.game.trivia.data.repository.QuestionsRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

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
    fun providesOkHttpClient(
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
    fun providesRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .baseUrl(BASE_URL)
            .build()
    }


    @Singleton
    @Provides
    fun providesQuestionsApiService(retrofit: Retrofit): QuestionsAPI {
        return retrofit.create(QuestionsAPI::class.java)
    }


    @Singleton
    @Provides
    fun providesRepository(questionsAPI: QuestionsAPI): QuestionsRepository {
        return QuestionsRepositoryImpl(questionsAPI)
    }


}