package com.amsavarthan.game.trivia.di

import android.content.Context
import com.amsavarthan.game.trivia.data.api.ApiService
import com.amsavarthan.game.trivia.data.api.interceptor.NetworkInterceptor
import com.amsavarthan.game.trivia.repository.Repository
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
import javax.inject.Singleton

private const val BASE_URL = "https://opentdb.com/"

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun providesNetworkInterceptor(@ApplicationContext context: Context): NetworkInterceptor {
        return NetworkInterceptor(context)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesMoshiFactory(): MoshiConverterFactory {
        val moshi = Moshi.Builder().build()
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
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun providesRepository(apiService: ApiService): Repository {
        return Repository(apiService)
    }


}