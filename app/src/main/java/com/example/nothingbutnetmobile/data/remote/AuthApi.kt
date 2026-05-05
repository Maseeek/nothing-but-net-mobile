package com.example.nothingbutnetmobile.data.remote

import com.example.nothingbutnetmobile.data.remote.models.AuthResponse
import com.example.nothingbutnetmobile.data.remote.models.LoginRequest
import com.example.nothingbutnetmobile.data.remote.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}
