package com.example.nothingbutnetmobile.domain.repository

import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getLatestShotAnalysis(): Flow<ShotAnalysis?>
    fun getAllShotAnalyses(): Flow<List<ShotAnalysis>>
    suspend fun saveShotAnalysis(analysis: ShotAnalysis)
    suspend fun seedDatabase()
}
