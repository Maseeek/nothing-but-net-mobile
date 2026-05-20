package com.example.nothingbutnetmobile.domain.model

import com.example.nothingbutnetmobile.data.local.entity.SessionEntity

data class Session(
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

fun Session.toEntity(): SessionEntity {
    return SessionEntity(
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

fun calculateLongestStreak(results: List<Int>): Int {
    var maxStreak = 0
    var currentStreak = 0
    for (res in results) {
        if (res == 1) {
            currentStreak++
            if (currentStreak > maxStreak) maxStreak = currentStreak
        } else {
            currentStreak = 0
        }
    }
    return maxStreak
}

fun calculateAverageMakeAngle(angles: List<Double>, results: List<Int>): Double {
    var total_ang = 0.0
    var cnt = 0
    for (i in 0 until angles.size) {
        val valAngle = angles[i]
        if (i < results.size && results[i] == 1 && valAngle > 0.0) {
            total_ang += valAngle
            cnt++
        }
    }
    return if (cnt > 0) total_ang / cnt else 0.0
}

fun calculateAverageMissAngle(angles: List<Double>, results: List<Int>): Double {
    var totalMissWithAngles = 0.0
    var missCounter = 0
    for (idx in angles.indices) {
        val valAngle = angles[idx]
        if (idx < results.size && results[idx] == 0 && valAngle > 0.0) {
            totalMissWithAngles += valAngle
            missCounter++
        }
    }
    return if (missCounter > 0) totalMissWithAngles / missCounter else 0.0
}


