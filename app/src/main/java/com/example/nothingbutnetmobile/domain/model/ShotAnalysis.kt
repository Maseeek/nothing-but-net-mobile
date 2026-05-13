package com.example.nothingbutnetmobile.domain.model

data class ShotAnalysis(
    val totalShots: Int,
    val makes: Int,
    val misses: Int,
    val fgPercentage: Double,
    val longestStreak: Int,
    val averageAngle: Double,
    val averageMakeAngle: Double,
    val averageMissAngle: Double,
    val shotAngles: List<Double>,
    val shotsResults: List<Int>,
    val timestamp: Long
)
