package com.example.nothingbutnetmobile.data.remote.models

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    val userId: String?,
    val makes: Int,
    val misses: Int,
    val longestStreak: Int,
    val averageAngle: Double,
    val averageMakeAngle: Double,
    val averageMissAngle: Double,
    val fgPercentage: Double,
    val shotAngles: List<Double>,
    val shotsResults: List<Int>,
    val totalShots: Int
)

data class SessionResponse(
    val message: String,
    val session: SessionData
)

data class SessionData(
    @SerializedName("_id") val id: String,
    val userId: String?,
    val sessionDate: String,
    val makes: Int,
    val misses: Int,
    val longestStreak: Int,
    val fgPercentage: Double,
    val totalShots: Int,
    val averageAngle: Double? = 0.0,
    val averageMakeAngle: Double? = 0.0,
    val averageMissAngle: Double? = 0.0,
    val shotAngles: List<Double>? = emptyList(),
    val shotsResults: List<Int>? = emptyList()
)

data class FgPercentageResponse(
    val fieldGoalPercentage: String
)

data class LongestStreakResponse(
    val longestStreak: Int
)
