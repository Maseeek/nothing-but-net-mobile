package com.example.nothingbutnetmobile.data.repository

import com.example.nothingbutnetmobile.data.local.dao.ShotAnalysisDao
import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity
import com.example.nothingbutnetmobile.data.remote.AuthApi
import com.example.nothingbutnetmobile.data.remote.models.SessionRequest
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val shotAnalysisDao: ShotAnalysisDao,
    private val authApi: AuthApi,
    private val authRepository: AuthRepository
) : StatsRepository {

    override fun getLatestShotAnalysis(): Flow<ShotAnalysis?> {
        return shotAnalysisDao.getLatestAnalysis().map { entity ->
            entity?.let {
                ShotAnalysis(
                    totalShots = it.totalShots,
                    makes = it.makes,
                    misses = it.misses,
                    fgPercentage = it.fgPercentage,
                    longestStreak = it.longestStreak,
                    averageAngle = it.averageAngle,
                    averageMakeAngle = it.averageMakeAngle,
                    averageMissAngle = it.averageMissAngle,
                    shotAngles = it.shotAngles,
                    shotsResults = it.shotsResults,
                    timestamp = it.timestamp
                )
            }
        }
    }

    override fun getAllShotAnalyses(): Flow<List<ShotAnalysis>> {
        return shotAnalysisDao.getAllAnalyses().map { entities ->
            entities.map {
                ShotAnalysis(
                    totalShots = it.totalShots,
                    makes = it.makes,
                    misses = it.misses,
                    fgPercentage = it.fgPercentage,
                    longestStreak = it.longestStreak,
                    averageAngle = it.averageAngle,
                    averageMakeAngle = it.averageMakeAngle,
                    averageMissAngle = it.averageMissAngle,
                    shotAngles = it.shotAngles,
                    shotsResults = it.shotsResults,
                    timestamp = it.timestamp
                )
            }
        }
    }

    override suspend fun saveShotAnalysis(analysis: ShotAnalysis) {
        shotAnalysisDao.insertAnalysis(
            ShotAnalysisEntity(
                totalShots = analysis.totalShots,
                makes = analysis.makes,
                misses = analysis.misses,
                fgPercentage = analysis.fgPercentage,
                longestStreak = analysis.longestStreak,
                averageAngle = analysis.averageAngle,
                averageMakeAngle = analysis.averageMakeAngle,
                averageMissAngle = analysis.averageMissAngle,
                shotAngles = analysis.shotAngles,
                shotsResults = analysis.shotsResults,
                timestamp = analysis.timestamp
            )
        )
    }

    override suspend fun seedDatabase() {
        // No longer seeding dummy data automatically
    }

    override suspend fun clearAll() {
        shotAnalysisDao.deleteAll()
    }

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            android.util.Log.d("StatsRepository", "Starting sync with server...")
            val userId = authRepository.getUserId() 
            if (userId == null) {
                android.util.Log.e("StatsRepository", "Sync failed: User not logged in")
                return Result.failure(Exception("User not logged in"))
            }
            
            android.util.Log.d("StatsRepository", "Fetching sessions for userId: $userId")
            val response = authApi.getSessions(userId)
            
            if (response.isSuccessful) {
                val sessions = response.body() ?: emptyList()
                android.util.Log.d("StatsRepository", "Received ${sessions.size} sessions from server")
                
                shotAnalysisDao.deleteAll()
                sessions.forEach { session ->
                    val formats = listOf(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        "yyyy-MM-dd'T'HH:mm:ssZ"
                    )
                    
                    var timestamp = System.currentTimeMillis()
                    for (format in formats) {
                        try {
                            val sdf = SimpleDateFormat(format, Locale.US)
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val date = sdf.parse(session.sessionDate)
                            if (date != null) {
                                timestamp = date.time
                                break
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }

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
                android.util.Log.d("StatsRepository", "Sync completed successfully")
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string() ?: response.message()
                android.util.Log.e("StatsRepository", "Sync failed: $error")
                Result.failure(Exception("Sync failed: $error"))
            }
        } catch (e: Exception) {
            android.util.Log.e("StatsRepository", "Sync exception", e)
            Result.failure(e)
        }
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
