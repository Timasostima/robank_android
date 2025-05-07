package es.timasostima.robank.database

import android.util.Log
import es.timasostima.robank.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoalManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val apiClient = RetrofitClient.apiService

    private val _goalsState = MutableStateFlow<List<GoalData>>(emptyList())
    val goalsState: StateFlow<List<GoalData>> = _goalsState

    init {
        loadGoals()
    }

    fun loadGoals() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.getGoals()
                if (response.isSuccessful) {
                    _goalsState.value = response.body() ?: emptyList()
                } else {
                    Log.e("GoalManager", "Failed to load goals: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalManager", "Error loading goals from API", e)
            }
        }
    }

    fun createGoal(goal: GoalDTO) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val nextIndex = calculateNextIndex()
                val response = apiClient.createGoal(goal.copy(index = nextIndex))
                if (response.isSuccessful) {
                    val createdGoal = response.body()
                    if (createdGoal != null) {
                        _goalsState.value = _goalsState.value + createdGoal
                    } else {
                        Log.e("GoalManager", "Failed to create goal: Response body is null")
                    }
                } else {
                    Log.e("GoalManager", "Failed to create goal: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalManager", "Error creating goal", e)
            }
        }
    }

    fun updateGoal(goalId: Int, name: String, price: Double) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.updateGoal(goalId, GoalDTO(name, price, index = 0))
                if (response.isSuccessful) {
                    _goalsState.value = _goalsState.value.map {
                        if (it.id == goalId) it.copy(name = name, price = price) else it
                    }
                } else {
                    Log.e("GoalManager", "Failed to update goal: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalManager", "Error updating goal", e)
            }
        }
    }

    fun deleteGoal(goalId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.deleteGoal(goalId)
                if (response.isSuccessful) {
                    _goalsState.value = _goalsState.value.filter { it.id != goalId }
                } else {
                    Log.e("GoalManager", "Failed to delete goal: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalManager", "Error deleting goal", e)
            }
        }
    }

    private fun calculateNextIndex(): Int {
        return (_goalsState.value.maxByOrNull { it.index }?.index ?: 0) + 1
    }
}