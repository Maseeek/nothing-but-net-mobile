package com.example.nothingbutnetmobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nothingbutnetmobile.data.local.dao.ShotAnalysisDao
import com.example.nothingbutnetmobile.data.local.entity.ShotAnalysisEntity

@Database(entities = [ShotAnalysisEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shotAnalysisDao(): ShotAnalysisDao
}
