package com.amsavarthan.game.trivia.data.api

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Idle<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    class Success<T>(data: T, message: String? = null) : Resource<T>(data, message)
    class Error<T>(message: String) : Resource<T>(message = message)
    class Exception<T>(message: String) : Resource<T>(message = message)
}
