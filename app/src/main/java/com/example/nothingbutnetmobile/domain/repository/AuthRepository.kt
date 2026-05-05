package com.example.nothingbutnetmobile.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    fun isLoggedIn(): Boolean
    fun logout()
}
