package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.local.AuthStorage
import com.example.myapplication.data.remote.NetworkClient
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UrlRepository

class LilUrlApp : Application() {
    val authStorage by lazy { AuthStorage(this) }
    private val apiService by lazy { NetworkClient.create(authStorage) }
    val authRepository by lazy { AuthRepository(apiService, authStorage) }
    val urlRepository by lazy { UrlRepository(apiService) }
}
