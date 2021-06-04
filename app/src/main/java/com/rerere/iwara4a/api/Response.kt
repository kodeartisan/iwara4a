package com.rerere.iwara4a.api

sealed class Response<T>(
    private val data: T? = null,
    private val errorMessage: String? = null
) {
    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> failed(errorMessage: String = "null") = Failed<T>(errorMessage)
    }

    fun isSuccess() = this is Success
    fun isFailed() = this is Failed

    fun read() = data!!
    fun errorMessage() = errorMessage!!

    class Success<T> internal constructor(data: T): Response<T>(data = data)
    class Failed<T> internal constructor(errorMessage: String): Response<T>(errorMessage = errorMessage)
}