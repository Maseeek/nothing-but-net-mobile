package com.example.nothingbutnetmobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.domain.repository.AuthRepository
import com.example.nothingbutnetmobile.domain.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.getUser()
        _uiState.value = _uiState.value.copy(
            userName = user?.username ?: "User"
        )
        
        observeStats()
    }

    private fun observeStats() {
        statsRepository.getAllShotAnalyses()
            .onEach { analyses ->
                if (analyses.isNotEmpty()) {
                    val totalShots = analyses.sumOf { it.totalShots }
                    val totalMakes = analyses.sumOf { it.makes }
                    val totalMisses = analyses.sumOf { it.misses }
                    val careerFgPercentage = if (totalShots > 0) {
                        (totalMakes.toDouble() / totalShots.toDouble()) * 100
                    } else 0.0
                    
                    val bestSession = analyses.maxByOrNull { it.fgPercentage }?.fgPercentage ?: 0.0
                    
                    val history = analyses.sortedBy { it.timestamp }.takeLast(5).map { it.fgPercentage.toFloat() }
                    
                    val sortedAnalyses = analyses.sortedByDescending { it.timestamp }
                    val lastSessionFg = if (sortedAnalyses.isNotEmpty()) sortedAnalyses[0].fgPercentage else 0.0
                    val previousSessionFg = if (sortedAnalyses.size > 1) sortedAnalyses[1].fgPercentage else 0.0
                    val diff = lastSessionFg - previousSessionFg
                    
                    val progressMessage = if (sortedAnalyses.size > 1) {
                        val prevStr = String.format("%.1f", previousSessionFg)
                        val lastStr = String.format("%.1f", lastSessionFg)
                        if (diff >= 0) {
                            "Your FG% went from $prevStr% to $lastStr%. Great improvement!"
                        } else {
                            "Your FG% went from $prevStr% to $lastStr%. Keep practicing!"
                        }
                    } else {
                        "Keep practicing to see your progress!"
                    }

                    _uiState.value = _uiState.value.copy(
                        careerFgPercentage = careerFgPercentage,
                        totalShots = totalShots,
                        totalMakes = totalMakes,
                        totalMisses = totalMisses,
                        bestSessionFgPercentage = bestSession,
                        progressMessage = progressMessage,
                        fgHistory = history,
                        recentSessions = analyses.sortedByDescending { it.timestamp }.take(3)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout()
    }
}

data class ProfileUiState(
    val userName: String = "User",
    val careerFgPercentage: Double = 0.0,
    val totalShots: Int = 0,
    val totalMakes: Int = 0,
    val totalMisses: Int = 0,
    val bestSessionFgPercentage: Double = 0.0,
    val progressMessage: String = "Keep practicing to see your progress!",
    val fgHistory: List<Float> = emptyList(),
    val recentSessions: List<ShotAnalysis> = emptyList()
)
