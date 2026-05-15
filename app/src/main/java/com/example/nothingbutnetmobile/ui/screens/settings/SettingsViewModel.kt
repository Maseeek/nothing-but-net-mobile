package com.example.nothingbutnetmobile.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nothingbutnetmobile.data.local.TokenManager
import com.example.nothingbutnetmobile.data.local.PreferenceManager
import com.example.nothingbutnetmobile.ui.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val targetAngle: Float = 55f,
    val userName: String = "",
    val appVersion: String = "1.0.0",
    val cacheSize: String = "0 Bytes"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    userName = tokenManager.getUsername() ?: "User",
                    darkTheme = preferenceManager.getDarkTheme(),
                    notificationsEnabled = preferenceManager.getNotificationsEnabled(),
                    showShotAngles = preferenceManager.getShowShotAngles(),
                    targetAngle = preferenceManager.getTargetAngle(),
                    cacheSize = FileUtils.getCacheSize(context)
                )
            }
        }
    }

    fun refreshCacheSize() {
        _uiState.update { it.copy(cacheSize = FileUtils.getCacheSize(context)) }
    }

    fun clearCache() {
        FileUtils.clearCache(context)
        refreshCacheSize()
    }

    fun toggleDarkTheme(enabled: Boolean) {
        preferenceManager.setDarkTheme(enabled)
        _uiState.update { it.copy(darkTheme = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        preferenceManager.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleShotAngles(enabled: Boolean) {
        preferenceManager.setShowShotAngles(enabled)
        _uiState.update { it.copy(showShotAngles = enabled) }
    }

    fun setTargetAngle(angle: Float) {
        preferenceManager.setTargetAngle(angle)
        _uiState.update { it.copy(targetAngle = angle) }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
