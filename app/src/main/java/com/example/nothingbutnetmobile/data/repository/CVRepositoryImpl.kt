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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class CVRepositoryImpl @Inject constructor(
    private val cvApi: CVApi,
    private val statsRepository: StatsRepository
) : CVRepository {

    override suspend fun analyzeVideo(
        videoFile: File,
        hoopLeft: List<Int>,
        hoopRight: List<Int>,
        showAngle: Boolean,
        targetAngle: Float
    ): Result<AnalysisResponse> = withContext(Dispatchers.IO) {
        try {
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                videoFile.name,
                videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            )

            val gson = Gson()
            val hoopLeftBody = gson.toJson(hoopLeft).toRequestBody("text/plain".toMediaTypeOrNull())
            val hoopRightBody = gson.toJson(hoopRight).toRequestBody("text/plain".toMediaTypeOrNull())
            val showAngleBody = showAngle.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val targetAngleBody = targetAngle.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            Log.d("CVRepository", "Starting video upload: ${videoFile.name}, size: ${videoFile.length()}")
            val response = cvApi.uploadAndAnalyze(
                videoPart,
                hoopLeftBody,
                hoopRightBody,
                showAngleBody,
                targetAngleBody
            )
            Log.d("CVRepository", "Server response received: ${response.code()}")

            val body = response.body()
            if (response.isSuccessful && body != null) {
                Log.d("CVRepository", "Analysis successful, processing results")
                // save local & sync
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
                    statsRepository.pushSessionToServer(analysis)
                }
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Log.e("CVRepository", "Analysis failed: $errorMsg")
                Result.failure(Exception("Analysis failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e("CVRepository", "Exception during analysis", e)
            Result.failure(e)
        }
    }
}
