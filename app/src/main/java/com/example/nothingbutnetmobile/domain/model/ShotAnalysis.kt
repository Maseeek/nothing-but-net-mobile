package com.example.nothingbutnetmobile.domain.model

import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity

data class ShotAnalysis(
    val id: Long = 0,
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

fun ShotAnalysis.toEntity(): ShotAnalysisEntity {
    return ShotAnalysisEntity(
        id = id,
        totalShots = totalShots,
        makes = makes,
        misses = misses,
        fgPercentage = fgPercentage,
        longestStreak = longestStreak,
        averageAngle = averageAngle,
        averageMakeAngle = averageMakeAngle,
        averageMissAngle = averageMissAngle,
        shotAngles = shotAngles,
        shotsResults = shotsResults,
        timestamp = timestamp
    )
}
