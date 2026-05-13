package com.example.nothingbutnetmobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.getUser()
        _uiState.value = _uiState.value.copy(
            userName = user?.username ?: "User"
        )
        
        observeStats()
        
        viewModelScope.launch {
            statsRepository.seedDatabase()
        }
    }

    private fun observeStats() {
        statsRepository.getLatestShotAnalysis()
            .onEach { analysis ->
                analysis?.let {
                    _uiState.value = _uiState.value.copy(
                        totalShots = it.totalShots.toString(),
                        longestStreak = it.longestStreak.toString(),
                        avgAngle = "${it.averageAngle}°",
                        fgPercentage = it.fgPercentage.toInt(),
                        fgRatio = "${it.makes}/${it.totalShots}"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val title: String = "Welcome to Nothing But Net!",
    val userName: String = "User",
    val totalShots: String = "0",
    val longestStreak: String = "0",
    val avgAngle: String = "0.0°",
    val fgPercentage: Int = 0,
    val fgRatio: String = "0/0"
)
