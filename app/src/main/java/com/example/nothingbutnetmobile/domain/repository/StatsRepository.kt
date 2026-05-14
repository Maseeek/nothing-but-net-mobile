package com.example.nothingbutnetmobile.domain.repository

import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getLatestShotAnalysis(): Flow<ShotAnalysis?>
    fun getAllShotAnalyses(): Flow<List<ShotAnalysis>>
    suspend fun getShotAnalysisById(id: Long): ShotAnalysis?
    suspend fun saveShotAnalysis(analysis: ShotAnalysis)
    suspend fun seedDatabase()
    suspend fun clearAll()
    
    // Server Sync
    suspend fun syncWithServer(): Result<Unit>
    suspend fun pushSessionToServer(analysis: ShotAnalysis): Result<Unit>
}
