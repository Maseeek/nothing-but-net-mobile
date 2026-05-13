package com.example.nothingbutnetmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
