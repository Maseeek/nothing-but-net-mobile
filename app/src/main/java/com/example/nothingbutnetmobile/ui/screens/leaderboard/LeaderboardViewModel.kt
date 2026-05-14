package com.example.nothingbutnetmobile.ui.screens.leaderboard

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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

enum class LeaderboardSort {
    SHOTS, FG_PERCENTAGE, OPTIMAL_ARC
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.getUser()
        _uiState.value = _uiState.value.copy(
            userName = user?.username ?: "User"
        )
        
        observeLeaderboard()
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            statsRepository.syncWithServer()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun observeLeaderboard() {
        statsRepository.getAllShotAnalyses()
            .onEach { analyses ->
                _uiState.value = _uiState.value.copy(
                    allSessions = analyses
                )
                updateSortedList()
            }
            .launchIn(viewModelScope)
    }

    fun setSortType(sortType: LeaderboardSort) {
        _uiState.value = _uiState.value.copy(sortType = sortType)
        updateSortedList()
    }

    private fun updateSortedList() {
        val sessions = _uiState.value.allSessions
        val sorted = when (_uiState.value.sortType) {
            LeaderboardSort.SHOTS -> sessions.sortedByDescending { it.totalShots }
            LeaderboardSort.FG_PERCENTAGE -> sessions.sortedByDescending { it.fgPercentage }
            LeaderboardSort.OPTIMAL_ARC -> sessions.sortedBy { abs(it.averageAngle - 55.0) }
        }
        _uiState.value = _uiState.value.copy(rankedSessions = sorted)
    }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "User",
    val sortType: LeaderboardSort = LeaderboardSort.SHOTS,
    val allSessions: List<ShotAnalysis> = emptyList(),
    val rankedSessions: List<ShotAnalysis> = emptyList()
)
