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
        private const val USERNAME = "username"
        private const val USER_ID = "user_id"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(AUTH_TOKEN).remove(USERNAME).remove(USER_ID).apply()
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
