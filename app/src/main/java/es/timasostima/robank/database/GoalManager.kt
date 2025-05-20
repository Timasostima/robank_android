package es.timasostima.robank.database

import android.util.Log
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.dto.GoalDTO
import es.timasostima.robank.dto.GoalData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.IOException

class GoalManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val apiClient = RetrofitClient.apiService

    private val _goalsState = MutableStateFlow<List<GoalData>>(emptyList())
    val goalsState: StateFlow<List<GoalData>> = _goalsState

    private val imageCache = mutableMapOf<Int, Bitmap>()

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

    suspend fun uploadGoalImage(goalId: Int, uri: Uri) {
        try {
            val file = createTempFileFromUri(uri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = apiClient.uploadGoalImage(goalId, body)
            if (!response.isSuccessful) {
                Log.e("GoalManager", "Failed to upload image: ${response.errorBody()?.string()}")
            } else {
                // Update the goal in the local state
                _goalsState.value = _goalsState.value.map {
                    if (it.id == goalId) {
                        it.copy(imageUrl = response.body()?.get("filename"))
                    } else {
                        it
                    }
                }
            }
            
            // Delete temp file
            file.delete()
        } catch (e: Exception) {
            Log.e("GoalManager", "Error uploading goal image", e)
        }
    }

    suspend fun getGoalImage(goalId: Int): Bitmap? {
        // Check cache first
        imageCache[goalId]?.let { return it }

        return try {
            val response = apiClient.getGoalImage(goalId)
            val inputStream = response.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Cache the bitmap for future use
            bitmap?.let { imageCache[goalId] = it }
            bitmap
        } catch (e: Exception) {
            Log.e("GoalManager", "Error getting goal image", e)
            null
        }
    }

    fun preloadFirstGoalImage() {
        coroutineScope.launch(Dispatchers.IO) {
            val firstGoalId = _goalsState.value.firstOrNull()?.id ?: return@launch
            getGoalImage(firstGoalId) // This will cache the image
        }
    }

    fun clearImageCache() {
        imageCache.clear()
    }
    
    private suspend fun createTempFileFromUri(uri: Uri): File = withContext(Dispatchers.IO) {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Failed to open input stream for URI: $uri")
        
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        
        return@withContext tempFile
    }

    private fun calculateNextIndex(): Int {
        return (_goalsState.value.maxByOrNull { it.index }?.index?.plus(1) ?: 0)
    }
}