package com.example.nothingbutnetmobile.data.remote.models

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    val userId: String?,
    val makes: Int,
    val misses: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("average_angle") val averageAngle: Double,
    @SerializedName("average_make_angle") val averageMakeAngle: Double,
    @SerializedName("average_miss_angle") val averageMissAngle: Double,
    @SerializedName("fg_percentage") val fgPercentage: Double,
    @SerializedName("shot_angles") val shotAngles: List<Double>,
    @SerializedName("shots_results") val shotsResults: List<Int>,
    @SerializedName("total_shots") val totalShots: Int
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
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("fg_percentage") val fgPercentage: Double,
    @SerializedName("total_shots") val totalShots: Int
)

data class FgPercentageResponse(
    val fieldGoalPercentage: String
)

data class LongestStreakResponse(
    val longestStreak: Int
)
