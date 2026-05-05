package com.example.nothingbutnetmobile.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun register(username: String, email: String, password: String): Result<String>
    fun isLoggedIn(): Boolean
    fun logout()
}
