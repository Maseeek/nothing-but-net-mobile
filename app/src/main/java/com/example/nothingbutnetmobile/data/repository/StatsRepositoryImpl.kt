package com.example.nothingbutnetmobile.data.repository

import com.example.nothingbutnetmobile.data.local.dao.ShotAnalysisDao
import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val shotAnalysisDao: ShotAnalysisDao
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
                timestamp = analysis.timestamp
            )
        )
    }

    override suspend fun seedDatabase() {
        // Insert a mock session if needed
        saveShotAnalysis(
            ShotAnalysis(
                totalShots = 25,
                makes = 18,
                misses = 7,
                fgPercentage = 72.0,
                longestStreak = 6,
                averageAngle = 52.4,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
