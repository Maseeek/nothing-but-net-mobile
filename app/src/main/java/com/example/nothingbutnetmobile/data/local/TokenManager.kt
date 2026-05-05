package com.example.nothingbutnetmobile.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(AUTH_TOKEN).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
