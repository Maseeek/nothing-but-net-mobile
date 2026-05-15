package com.example.nothingbutnetmobile.domain.repository

import com.example.nothingbutnetmobile.data.remote.models.AnalysisResponse
import java.io.File

interface CVRepository {
    /**
     * Uploads a video for basketball shot analysis.
     * 
     * @param videoFile The video file to analyze.
     * @param hoopLeft Coordinates of the left side of the hoop [x, y].
     * @param hoopRight Coordinates of the right side of the hoop [x, y].
     * @param showAngle Whether to return detailed angle data.
     */
    suspend fun analyzeVideo(
        videoFile: File,
        hoopLeft: List<Int>,
        hoopRight: List<Int>,
        showAngle: Boolean = false,
        targetAngle: Float = 55f
    ): Result<AnalysisResponse>
}
