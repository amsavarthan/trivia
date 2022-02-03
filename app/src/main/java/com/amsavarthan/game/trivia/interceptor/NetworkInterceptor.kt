package com.amsavarthan.game.trivia.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class NoConnectivityException(message: String) : IOException(message)

class NetworkInterceptor @Inject constructor(@ApplicationContext context: Context) : Interceptor {

    private val connectivityManager by lazy {
        ContextCompat.getSystemService(
            context, ConnectivityManager::class.java
        )
    }

    private fun isConnectionAvailable(): Boolean {
        val network = connectivityManager?.activeNetwork
        val connection = connectivityManager?.getNetworkCapabilities(network)

        return connection != null
                && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isInternetAvailable() = try {
        val timeoutMs = 1500
        val sock = Socket()
        val address = InetSocketAddress("8.8.8.8", 53)

        sock.connect(address, timeoutMs)
        sock.close()

        true
    } catch (e: IOException) {
        false
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionAvailable()) {
            throw NoConnectivityException("No network available, please check your WiFi or Data connection")
        } else if (!isInternetAvailable()) {
            throw NoConnectivityException("No internet available, please check your connected WIFi or Data")
        } else {
            chain.proceed(chain.request())
        }
    }

}