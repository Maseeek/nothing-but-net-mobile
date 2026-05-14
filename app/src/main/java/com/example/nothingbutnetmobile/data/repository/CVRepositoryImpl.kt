package com.example.nothingbutnetmobile.data.repository

import com.example.nothingbutnetmobile.data.remote.CVApi
import com.example.nothingbutnetmobile.data.remote.models.AnalysisResponse
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.domain.repository.CVRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CVRepositoryImpl @Inject constructor(
    private val cvApi: CVApi,
    private val statsRepository: StatsRepository
) : CVRepository {

    override suspend fun analyzeVideo(
        videoFile: File,
        hoopLeft: List<Int>,
        hoopRight: List<Int>,
        showAngle: Boolean
    ): Result<AnalysisResponse> {
        return try {
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                videoFile.name,
                videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            )

            val gson = Gson()
            val hoopLeftBody = gson.toJson(hoopLeft).toRequestBody("text/plain".toMediaTypeOrNull())
            val hoopRightBody = gson.toJson(hoopRight).toRequestBody("text/plain".toMediaTypeOrNull())
            val showAngleBody = showAngle.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = cvApi.uploadAndAnalyze(
                videoPart,
                hoopLeftBody,
                hoopRightBody,
                showAngleBody
            )

            val body = response.body()
            if (response.isSuccessful && body != null) {
                // Save to database if data is present
                body.data?.let { result ->
                    val analysis = ShotAnalysis(
                        totalShots = result.totalShots,
                        makes = result.makes,
                        misses = result.misses,
                        fgPercentage = result.fgPercentage,
                        longestStreak = result.longestStreak,
                        averageAngle = result.averageAngle,
                        averageMakeAngle = result.averageMakeAngle,
                        averageMissAngle = result.averageMissAngle,
                        shotAngles = result.shotAngles ?: emptyList(),
                        shotsResults = result.shotsResults ?: emptyList(),
                        timestamp = System.currentTimeMillis()
                    )
                    statsRepository.saveShotAnalysis(analysis)
                    
                    // Push to Node.js server
                    statsRepository.pushSessionToServer(analysis)
                }
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Analysis failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
