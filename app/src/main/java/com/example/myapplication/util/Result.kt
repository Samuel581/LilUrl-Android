package com.example.myapplication.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = -1) : Result<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> Result<T>): Result<T> = try {
    call()
} catch (e: Exception) {
    Result.Error(e.message ?: "Network error. Check your connection.")
}
