package com.example.nothingbutnetmobile.ui.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.CVRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
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
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.getUser()
        _uiState.value = _uiState.value.copy(
            userName = user?.username ?: "User"
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

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = AnalysisStatus.LOADING)
            
            val result = cvRepository.analyzeVideo(
                videoFile = videoFile,
                hoopLeft = currentLeft,
                hoopRight = currentRight,
                showAngle = true
            )

            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.SUCCESS,
                    analysisResult = "Analysis Complete: ${response.data?.makes ?: 0}/${response.data?.totalShots ?: 0} Shots Made",
                    shotAngles = response.data?.shotAngles ?: emptyList(),
                    shotsResults = response.data?.shotsResults ?: emptyList()
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.ERROR,
                    errorMessage = error.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun loadLatestAnalysis() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = AnalysisStatus.LOADING)
            
            // Try to sync with server first
            val syncResult = statsRepository.syncWithServer()
            if (syncResult.isFailure) {
                // If sync fails, it might be a connection issue
                _uiState.value = _uiState.value.copy(
                    status = AnalysisStatus.ERROR,
                    errorMessage = "Connection Error: Could not reach the server to fetch latest data."
                )
                return@launch
            }

            // Get all analyses to find today's data
            statsRepository.getAllShotAnalyses().collect { allAnalyses ->
                val now = System.currentTimeMillis()
                val todayAnalyses = allAnalyses.filter { isSameDay(it.timestamp, now) }
                
                val selected = todayAnalyses.firstOrNull() ?: allAnalyses.firstOrNull()
                
                if (selected != null) {
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.SUCCESS,
                        selectedAnalysis = selected,
                        recentAnalyses = allAnalyses.take(5),
                        analysisResult = "Latest Session: ${selected.makes}/${selected.totalShots} Shots Made",
                        shotAngles = selected.shotAngles,
                        shotsResults = selected.shotsResults
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
                // Also get all analyses for the recent list
                statsRepository.getAllShotAnalyses().collect { allAnalyses ->
                    _uiState.value = _uiState.value.copy(
                        status = AnalysisStatus.SUCCESS,
                        selectedAnalysis = selected,
                        recentAnalyses = allAnalyses.take(5),
                        analysisResult = "Viewing Analysis: ${selected.makes}/${selected.totalShots} Shots Made",
                        shotAngles = selected.shotAngles,
                        shotsResults = selected.shotsResults
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
        _uiState.value = _uiState.value.copy(
            selectedAnalysis = analysis,
            shotAngles = analysis.shotAngles,
            shotsResults = analysis.shotsResults
        )
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
    val selectedAnalysis: ShotAnalysis? = null,
    val recentAnalyses: List<ShotAnalysis> = emptyList()
)
