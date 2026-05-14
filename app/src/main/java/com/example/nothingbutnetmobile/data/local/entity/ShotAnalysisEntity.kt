package com.example.nothingbutnetmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.example.nothingbutnetmobile.domain.model.ShotAnalysis

@Entity(tableName = "shot_analysis")
data class ShotAnalysisEntity(
    @PrimaryKey(autoGenerate = true)
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
    val timestamp: Long = System.currentTimeMillis()
)

fun ShotAnalysisEntity.toDomain(): ShotAnalysis {
    return ShotAnalysis(
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
