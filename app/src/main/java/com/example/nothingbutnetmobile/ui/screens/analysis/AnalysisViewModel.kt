package com.example.nothingbutnetmobile.ui.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.CVRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class AnalysisStatus {
    IDLE, SELECTING_LEFT, SELECTING_RIGHT, READY, LOADING, SUCCESS, ERROR
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cvRepository: CVRepository
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
    val shotsResults: List<Int> = emptyList()
)
