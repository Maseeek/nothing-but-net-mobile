package com.example.nothingbutnetmobile.data.repository

import com.example.nothingbutnetmobile.data.local.dao.ShotAnalysisDao
import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity
import com.example.nothingbutnetmobile.data.local.entity.toDomain
import com.example.nothingbutnetmobile.data.remote.AuthApi
import com.example.nothingbutnetmobile.data.remote.models.SessionRequest
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.domain.model.toEntity
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val shotAnalysisDao: ShotAnalysisDao,
    private val authApi: AuthApi,
    private val authRepository: AuthRepository
) : StatsRepository {

    override fun getLatestShotAnalysis(): Flow<ShotAnalysis?> {
        return shotAnalysisDao.getLatestAnalysis().map { it?.toDomain() }
    }

    override fun getAllShotAnalyses(): Flow<List<ShotAnalysis>> {
        return shotAnalysisDao.getAllAnalyses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getShotAnalysisById(id: Long): ShotAnalysis? {
        return shotAnalysisDao.getAnalysisById(id)?.toDomain()
    }

    override suspend fun saveShotAnalysis(analysis: ShotAnalysis) {
        shotAnalysisDao.insertAnalysis(analysis.toEntity())
    }

    override suspend fun seedDatabase() {
        // No longer seeding dummy data automatically
    }

    override suspend fun clearAll() {
        shotAnalysisDao.deleteAll()
    }

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val userId = authRepository.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val response = authApi.getSessions(userId)
            
            if (response.isSuccessful) {
                val sessions = response.body() ?: emptyList()
                shotAnalysisDao.deleteAll()
                
                sessions.forEach { session ->
                    val timestamp = parseServerDate(session.sessionDate)
                    shotAnalysisDao.insertAnalysis(
                        ShotAnalysisEntity(
                            totalShots = session.totalShots,
                            makes = session.makes,
                            misses = session.misses,
                            fgPercentage = session.fgPercentage,
                            longestStreak = session.longestStreak,
                            averageAngle = 0.0,
                            averageMakeAngle = 0.0,
                            averageMissAngle = 0.0,
                            shotAngles = emptyList(),
                            shotsResults = emptyList(),
                            timestamp = timestamp
                        )
                    )
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sync failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("StatsRepository", "Sync error", e)
            Result.failure(e)
        }
    }

    private fun parseServerDate(dateStr: String): Long {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) { continue }
        }
        return System.currentTimeMillis()
    }

    override suspend fun pushSessionToServer(analysis: ShotAnalysis): Result<Unit> {
        return try {
            val userId = authRepository.getUserId()
            val request = SessionRequest(
                userId = userId,
                makes = analysis.makes,
                misses = analysis.misses,
                longestStreak = analysis.longestStreak,
                averageAngle = analysis.averageAngle,
                averageMakeAngle = analysis.averageMakeAngle,
                averageMissAngle = analysis.averageMissAngle,
                fgPercentage = analysis.fgPercentage,
                shotAngles = analysis.shotAngles,
                shotsResults = analysis.shotsResults,
                totalShots = analysis.totalShots
            )
            
            val response = authApi.saveSession(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Push failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
