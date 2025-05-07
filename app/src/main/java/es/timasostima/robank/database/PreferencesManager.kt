package es.timasostima.robank.database

import android.util.Log
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.config.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreferencesManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val apiClient = RetrofitClient.apiService

    private val _themeState = MutableStateFlow(ThemeMode.SYSTEM)
    val themeState: StateFlow<ThemeMode> = _themeState

    private val _preferencesState = MutableStateFlow<PreferencesData?>(null)
    val preferencesState: StateFlow<PreferencesData?> = _preferencesState

    init {
        loadPreferences()
    }

    fun loadPreferences() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val prefs = apiClient.getPreferences()
                _preferencesState.value = prefs
                _themeState.value = ThemeMode.Companion.fromString(prefs.theme)
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error loading preferences from API", e)
            }
        }
    }

    fun cycleTheme() {
        val currentTheme = themeState.value
        val nextTheme = when(currentTheme) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }

        updateTheme(nextTheme)
    }

    fun updateTheme(theme: ThemeMode) {
        _themeState.value = theme

        coroutineScope.launch(Dispatchers.IO) {
            try {
                apiClient.updatePreferences(mapOf("theme" to theme.value))
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error updating theme", e)
            }
        }
    }

    fun updateLanguage(language: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                apiClient.updatePreferences(mapOf("language" to language))

                _preferencesState.value?.let { prefs ->
                    _preferencesState.value = prefs.copy(language = language)
                }
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error updating language", e)
            }
        }
    }

    fun updateCurrency(currency: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                apiClient.updatePreferences(mapOf("currency" to currency))

                _preferencesState.value?.let { prefs ->
                    _preferencesState.value = prefs.copy(currency = currency)
                }
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error updating currency", e)
            }
        }
    }

    fun updateNotifications(enabled: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                apiClient.updatePreferences(mapOf("notifications" to enabled.toString()))

                _preferencesState.value?.let { prefs ->
                    _preferencesState.value = prefs.copy(notifications = enabled)
                }
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error updating notifications", e)
            }
        }
    }
}