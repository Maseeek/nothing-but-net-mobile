package com.example.nothingbutnetmobile.data.remote.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String?,
    val error: String?,
    val message: String?
)
