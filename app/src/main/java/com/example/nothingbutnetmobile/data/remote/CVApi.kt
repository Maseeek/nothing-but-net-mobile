package com.example.nothingbutnetmobile.data.remote

import com.example.nothingbutnetmobile.data.remote.models.AnalysisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CVApi {
    /**
     * Uploads a video and hoop coordinates for AI analysis.
     * 
     * @param video The video file as a MultipartBody.Part
     * @param hoopLeft JSON string for left hoop coordinates (e.g., "[x, y]")
     * @param hoopRight JSON string for right hoop coordinates (e.g., "[x, y]")
     * @param showAngle String "true" or "false" to include detailed angle data
     */
    @Multipart
    @POST("upload-and-analyze")
    suspend fun uploadAndAnalyze(
        @Part video: MultipartBody.Part,
        @Part("hoopLeft") hoopLeft: RequestBody,
        @Part("hoopRight") hoopRight: RequestBody,
        @Part("showAngle") showAngle: RequestBody
    ): Response<AnalysisResponse>
}
