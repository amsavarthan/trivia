package com.amsavarthan.game.trivia.data.api.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import com.amsavarthan.game.trivia.helper.NetworkHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class NoConnectivityException(
    message: String = "Please check your internet connection"
) : IOException(message)

class NetworkInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (NetworkHelper.isNetworkAvailable(context)) {
            return chain.proceed(chain.request())
        }
        throw NoConnectivityException()
    }

}