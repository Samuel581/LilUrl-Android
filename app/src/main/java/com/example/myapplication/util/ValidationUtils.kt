package com.example.myapplication.util

import android.util.Patterns
import java.util.regex.Pattern

object Validation {
    private val ALIAS_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{4,32}$")

    fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isValidPassword(password: String) = password.length >= 8
    fun isValidUrl(url: String) = url.startsWith("http://") || url.startsWith("https://")
            && Patterns.WEB_URL.matcher(url).matches()
    fun isValidAlias(alias: String) = ALIAS_PATTERN.matcher(alias).matches()
}
