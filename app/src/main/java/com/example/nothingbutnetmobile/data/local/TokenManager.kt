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
        private const val USERNAME = "userName"
        private const val USER_ID = "user_id"
    }

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearToken() {
        val editor = prefs.edit()
        editor.remove(AUTH_TOKEN)
        editor.remove(USERNAME)
        editor.remove(USER_ID)
        editor.apply()
    }

    fun saveUsername(username: String) {
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun saveUserId(userId: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}

