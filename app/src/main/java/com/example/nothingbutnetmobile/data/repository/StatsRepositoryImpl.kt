package com.example.nothingbutnetmobile.data.repository

import com.example.nothingbutnetmobile.data.local.dao.SessionDao
import com.example.nothingbutnetmobile.data.local.entity.SessionEntity
import com.example.nothingbutnetmobile.data.local.entity.toDomain
import com.example.nothingbutnetmobile.data.remote.AuthApi
import com.example.nothingbutnetmobile.data.remote.models.SessionRequest
import com.example.nothingbutnetmobile.data.remote.models.MongoObjectId
import com.example.nothingbutnetmobile.domain.model.Session
import com.example.nothingbutnetmobile.domain.model.toEntity
import com.example.nothingbutnetmobile.domain.model.calculateLongestStreak
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
    private val sessionDao: SessionDao,
    private val authApi: AuthApi,
    private val authRepository: AuthRepository
) : StatsRepository {

    override fun getLatestSession(): Flow<Session?> {
        return sessionDao.getLatestSession().map { it?.toDomain() }
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSessionById(id: Long): Session? {
        return sessionDao.getSessionById(id)?.toDomain()
    }

    override suspend fun saveSession(session: Session) {
        sessionDao.insertSession(session.toEntity())
    }

    override suspend fun seedDatabase() {
        // no longer seeding dummy data
    }

    override suspend fun clearAll() {
        sessionDao.deleteAll()
    }

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val userId = authRepository.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val response = authApi.getSessions(userId)
            
            if (response.isSuccessful) {
                val sessionsData = response.body() ?: emptyList()
                val sessionEntities = sessionsData.map { sessionData ->
                    val timestamp = parseServerDate(sessionData.sessionDate)
                    val results = sessionData.shotsResults ?: emptyList()
                    SessionEntity(
                        totalShots = sessionData.totalShots ?: (sessionData.makes + sessionData.misses),
                        makes = sessionData.makes,
                        misses = sessionData.misses,
                        fgPercentage = sessionData.fgPercentage ?: 0.0,
                        longestStreak = sessionData.longestStreak ?: calculateLongestStreak(results),
                        averageAngle = sessionData.averageAngle ?: 0.0,
                        averageMakeAngle = sessionData.averageMakeAngle ?: 0.0,
                        averageMissAngle = sessionData.averageMissAngle ?: 0.0,
                        shotAngles = sessionData.shotAngles ?: emptyList(),
                        shotsResults = results,
                        timestamp = timestamp
                    )
                }
                sessionDao.replaceAllSessions(sessionEntities)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Sync failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("StatsRepository", "Sync error", e)
            Result.failure(e)
        }
    }

    private fun parseServerDate(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return System.currentTimeMillis()
        
        val asLong = dateStr.toLongOrNull()
        if (asLong != null) {
            return if (asLong < 1000000000000L) asLong * 1000L else asLong
        }

        // Student-like workaround: replace T and Z to parse using simpler format
        val cleaned = dateStr.replace("T", " ").replace("Z", "")
        
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            return sdf.parse(cleaned)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            try {
                val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return sdf2.parse(cleaned)?.time ?: System.currentTimeMillis()
            } catch (ex: Exception) {
                return System.currentTimeMillis()
            }
        }
    }

    override suspend fun pushSessionToServer(session: Session): Result<Unit> {
        return try {
            val userId = authRepository.getUserId()
            val request = SessionRequest(
                userId = userId?.let { MongoObjectId(it) },
                makes = session.makes,
                misses = session.misses,
                longestStreak = session.longestStreak,
                averageAngle = session.averageAngle,
                averageMakeAngle = session.averageMakeAngle,
                averageMissAngle = session.averageMissAngle,
                fgPercentage = session.fgPercentage,
                shotAngles = session.shotAngles,
                shotsResults = session.shotsResults,
                totalShots = session.totalShots
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
