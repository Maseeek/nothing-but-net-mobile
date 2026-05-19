package com.example.nothingbutnetmobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.example.nothingbutnetmobile.domain.model.Session

@Entity(tableName = "sessions")
data class SessionEntity(
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

fun SessionEntity.toDomain(): Session {
    return Session(
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
