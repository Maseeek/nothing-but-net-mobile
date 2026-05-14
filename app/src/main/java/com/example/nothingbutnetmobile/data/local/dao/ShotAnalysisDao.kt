package com.example.nothingbutnetmobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotAnalysisDao {
    @Query("SELECT * FROM shot_analysis ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAnalysis(): Flow<ShotAnalysisEntity?>

    @Query("SELECT * FROM shot_analysis ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<ShotAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: ShotAnalysisEntity)

    @Query("SELECT * FROM shot_analysis WHERE id = :id")
    suspend fun getAnalysisById(id: Long): ShotAnalysisEntity?

    @Query("DELETE FROM shot_analysis")
    suspend fun deleteAll()
}
