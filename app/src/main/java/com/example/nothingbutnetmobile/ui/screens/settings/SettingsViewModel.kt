package com.example.nothingbutnetmobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val showShotAngles: Boolean = true,
    val userName: String = "",
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // In a real app, these would come from DataStore or SharedPreferences
            // For now, we use default values and the username from TokenManager
            _uiState.update { it.copy(userName = tokenManager.getUsername() ?: "User") }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        _uiState.update { it.copy(darkTheme = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleShotAngles(enabled: Boolean) {
        _uiState.update { it.copy(showShotAngles = enabled) }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
