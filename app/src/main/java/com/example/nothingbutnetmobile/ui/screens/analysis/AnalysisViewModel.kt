package com.example.nothingbutnetmobile.ui.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.CVRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.data.local.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class AnalysisStatus {
    IDLE, SELECTING_LEFT, SELECTING_RIGHT, READY, LOADING, SUCCESS, ERROR
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cvRepository: CVRepository,
    private val statsRepository: StatsRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.getUser()
        _uiState.value = _uiState.value.copy(
            userName = user?.username ?: "User",
            targetAngle = preferenceManager.getTargetAngle()
        )
    }

    fun setHoopLeft(x: Int, y: Int, normX: Float, normY: Float) {
        _uiState.value = _uiState.value.copy(
            hoopLeft = listOf(x, y),
            hoopLeftNormalized = Pair(normX, normY),
            status = AnalysisStatus.SELECTING_RIGHT
        )
    }

    fun setHoopRight(x: Int, y: Int, normX: Float, normY: Float) {
        _uiState.value = _uiState.value.copy(
            hoopRight = listOf(x, y),
            hoopRightNormalized = Pair(normX, normY),
            status = AnalysisStatus.READY
        )
    }

    fun confirmAnalysis(videoFile: File) {
        startAnalysis(videoFile)
    }

    fun startSelection() {
        _uiState.value = _uiState.value.copy(
            status = AnalysisStatus.SELECTING_LEFT,
            hoopLeft = null,
            hoopRight = null,
            hoopLeftNormalized = null,
            hoopRightNormalized = null
        )
    }

    fun startAnalysis(videoFile: File) {
        val currentLeft = _uiState.value.hoopLeft ?: listOf(100, 100)
        val currentRight = _uiState.value.hoopRight ?: listOf(200, 200)

        // check file size (50mb limit)
        val fileSizeMb = videoFile.length() / (1024 * 1024)
        if (fileSizeMb > 50) {
            _uiState.value = _uiState.value.copy(
                status = AnalysisStatus.ERROR,
                errorMessage = "Video is too large ($fileSizeMb MB). Please record a shorter video (under 50MB) for analysis."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = AnalysisStatus.LOADING)
            
            val result = cvRepository.analyzeVideo(
                videoFile = videoFile,
                hoopLeft = currentLeft,
                hoopRight = currentRight,
                showAngle = preferenceManager.getShowShotAngles(),
                targetAngle = preferenceManager.getTargetAngle()
            )

            result.onSuccess { response ->
                val data = response.data
                if (data != null) {
                    val results = data.shotsResults ?: emptyList()
                    val angles = data.shotAngles ?: emptyList()
                    
                    val newAnalysis = ShotAnalysis(
                        totalShots = data.totalShots,
                        makes = data.makes,
                        misses = data.misses,
                        fgPercentage = data.fgPercentage,
                        longestStreak = if (data.longestStreak > 0) data.longestStreak else calculateLongestStreak(results),
                        averageAngle = data.averageAngle,
                        averageMakeAngle = data.averageMakeAngle,
                        averageMissAngle = data.averageMissAngle,
                        shotAngles = angles,
                        shotsResults = results,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.SUCCESS,
                        selectedAnalysis = newAnalysis,
                        analysisResult = "Analysis Complete: ${data.makes}/${data.totalShots} Shots Made",
                        shotAngles = angles,
                        shotsResults = results
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.ERROR,
                        errorMessage = "Server returned empty analysis data."
                    )
                }
            }.onFailure { error ->
                val errorMsg = error.message ?: "Unknown error occurred"
                val displayMsg = if (errorMsg.contains("502")) {
                    "Server is overloaded or video is too complex. Please try a shorter video."
                } else {
                    errorMsg
                }
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.ERROR,
                    errorMessage = displayMsg
                )
            }
            
            // delete temp file
            try {
                if (videoFile.exists()) {
                    videoFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadLatestAnalysis() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = AnalysisStatus.LOADING)
            
            // sync with server
            val syncResult = statsRepository.syncWithServer()
            if (syncResult.isFailure) {
                // connection fail
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.ERROR,
                    errorMessage = "Connection Error: Could not reach the server to fetch latest data."
                )
                return@launch
            }

            // find today's data
            statsRepository.getAllShotAnalyses().collect { allAnalyses ->
                val now = System.currentTimeMillis()
                val todayAnalyses = allAnalyses.filter { isSameDay(it.timestamp, now) }
                
                val selected = todayAnalyses.firstOrNull() ?: allAnalyses.firstOrNull()
                
                if (selected != null) {
                    val processedSelected = if (selected.longestStreak == 0 && selected.shotsResults.isNotEmpty()) {
                        selected.copy(longestStreak = calculateLongestStreak(selected.shotsResults))
                    } else {
                        selected
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.SUCCESS,
                        selectedAnalysis = processedSelected,
                        recentAnalyses = allAnalyses.take(5),
                        analysisResult = "Latest Session: ${processedSelected.makes}/${processedSelected.totalShots} Shots Made",
                        shotAngles = processedSelected.shotAngles,
                        shotsResults = processedSelected.shotsResults
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.IDLE,
                        analysisResult = "No sessions found. Start by analyzing a video!"
                    )
                }
            }
        }
    }

    fun loadSpecificAnalysis(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = AnalysisStatus.LOADING)
            
            val selected = statsRepository.getShotAnalysisById(id)
            
            if (selected != null) {
                val processedSelected = if (selected.longestStreak == 0 && selected.shotsResults.isNotEmpty()) {
                    selected.copy(longestStreak = calculateLongestStreak(selected.shotsResults))
                } else {
                    selected
                }
                
                // get recent list
                statsRepository.getAllShotAnalyses().collect { allAnalyses ->
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.SUCCESS,
                        selectedAnalysis = processedSelected,
                        recentAnalyses = allAnalyses.take(5),
                        analysisResult = "Viewing Analysis: ${processedSelected.makes}/${processedSelected.totalShots} Shots Made",
                        shotAngles = processedSelected.shotAngles,
                        shotsResults = processedSelected.shotsResults
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.ERROR,
                    errorMessage = "Analysis session not found."
                )
            }
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun selectAnalysis(analysis: ShotAnalysis) {
        val processedAnalysis = if (analysis.longestStreak == 0 && analysis.shotsResults.isNotEmpty()) {
            analysis.copy(longestStreak = calculateLongestStreak(analysis.shotsResults))
        } else {
            analysis
        }
        
        _uiState.value = _uiState.value.copy(
            selectedAnalysis = processedAnalysis,
            shotAngles = processedAnalysis.shotAngles,
            shotsResults = processedAnalysis.shotsResults
        )
    }

    private fun calculateLongestStreak(results: List<Int>): Int {
        var maxStreak = 0
        var currentStreak = 0
        for (res in results) {
            if (res == 1) {
                currentStreak++
                if (currentStreak > maxStreak) maxStreak = currentStreak
            } else {
                currentStreak = 0
            }
        }
        return maxStreak
    }

    fun resetStatus() {
        _uiState.value = _uiState.value.copy(status = AnalysisStatus.IDLE, errorMessage = null)
    }
}

data class AnalysisUiState(
    val userName: String = "User",
    val status: AnalysisStatus = AnalysisStatus.IDLE,
    val analysisResult: String? = null,
    val errorMessage: String? = null,
    val hoopLeft: List<Int>? = null,
    val hoopRight: List<Int>? = null,
    val hoopLeftNormalized: Pair<Float, Float>? = null,
    val hoopRightNormalized: Pair<Float, Float>? = null,
    val shotAngles: List<Double> = emptyList(),
    val shotsResults: List<Int> = emptyList(),
    val targetAngle: Float = 55f,
    val selectedAnalysis: ShotAnalysis? = null,
    val recentAnalyses: List<ShotAnalysis> = emptyList()
)
