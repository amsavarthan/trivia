package com.amsavarthan.game.trivia.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object NetworkHelper {

    //TODO(Uncomment later)
    fun isNetworkAvailable(context: Context) =
        isConnectionAvailable(context)
//                && isInternetAvailable()

    private fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager = ContextCompat.getSystemService(
            context, ConnectivityManager::class.java
        )
        val network = connectivityManager?.activeNetwork
        val connection = connectivityManager?.getNetworkCapabilities(network)

        return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || connection.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val address = InetSocketAddress("8.8.8.8", 53)

            sock.connect(address, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }

}