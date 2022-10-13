package com.amsavarthan.game.trivia.data.api.interceptor

import com.amsavarthan.game.trivia.config.Config.HOST
import com.amsavarthan.game.trivia.config.Config.SCHEME
import com.amsavarthan.game.trivia.data.api.response.QuestionsResponseJsonAdapter
import com.amsavarthan.game.trivia.data.api.response.TokenResponseJsonAdapter
import com.amsavarthan.game.trivia.data.preference.TokenPreferencesRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class InvalidRequestException(message: String? = null) : IOException(message)
class InvalidTokenException(message: String) : IOException(message)

class TokenInterceptor @Inject constructor(
    private val tokenPreferencesRepository: TokenPreferencesRepository,
    private val moshi: Moshi
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenPreferencesRepository.fetchInitialPreferences().sessionToken
        }
        if (token.isBlank()) return chain.proceedGettingNewToken()

        val response = chain.proceedWithToken(token)
        val responseCode = QuestionsResponseJsonAdapter(moshi)
            .fromJson(response.body!!.string())
            ?.responseCode

        return when (responseCode) {
            null -> throw InvalidRequestException("Could not fetch questions")
            2 -> throw InvalidRequestException("Request failed with code 2")
            3 -> chain.proceedGettingNewToken()
            4 -> chain.proceedResettingToken(token)
            else -> chain.proceedWithToken(token)
        }

    }

    private fun Interceptor.Chain.proceedGettingNewToken(): Response {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment("api_token.php")
            .addQueryParameter("command", "request")
            .build()

        val token = proceedTokenRequest(url)
        return proceedWithToken(token)
    }

    private fun Interceptor.Chain.proceedResettingToken(token: String): Response {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment("api_token.php")
            .addQueryParameter("command", "reset")
            .addQueryParameter("token", token)
            .build()

        val newToken = proceedTokenRequest(url)
        return proceedWithToken(newToken)
    }

    private fun Interceptor.Chain.proceedTokenRequest(url: HttpUrl): String {
        val request = this.request().newBuilder().url(url).build()
        val response = proceed(request)

        if (!response.isSuccessful) throw InvalidTokenException("Token request failed")

        val tokenFromJson = TokenResponseJsonAdapter(moshi)
            .fromJson(response.body!!.string())
            ?.token

        if (tokenFromJson.isNullOrBlank()) throw InvalidTokenException("Token is null")

        runBlocking {
            tokenPreferencesRepository.saveToken(tokenFromJson)
        }
        return tokenFromJson
    }

    private fun Interceptor.Chain.proceedWithToken(
        token: String
    ): Response {
        val originalRequest = request()
        val urlWithToken = originalRequest.url.newBuilder()
            .addQueryParameter("token", token)
            .build()
        val request = originalRequest.newBuilder().url(urlWithToken).build()
        return proceed(request)
    }
}
