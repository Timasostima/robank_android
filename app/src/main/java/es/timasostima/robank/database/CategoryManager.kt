package es.timasostima.robank.database

import android.util.Log
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.dto.CategoryDTO
import es.timasostima.robank.dto.CategoryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryManager (
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
){
    private val apiClient = RetrofitClient.apiService

    private val _categoriesState = MutableStateFlow<List<CategoryData>>(emptyList())
    val categoriesState: StateFlow<List<CategoryData>> = _categoriesState

    init {
        loadCategories()
    }

    fun loadCategories() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.getCategories()
                if (response.isSuccessful) {
                    _categoriesState.value = response.body() ?: emptyList()
                } else {
                    Log.e("CategoryManager", "Failed to load categories: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CategoryManager", "Error loading categories from API: $e")
            }
        }
    }
    
    fun createCategory(category: CategoryDTO) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.createCategory(category)
                if (response.isSuccessful) {
                    val createdCategory = response.body()
                    if (createdCategory != null) {
                        _categoriesState.value = _categoriesState.value + createdCategory
                    } else {
                        Log.e("CategoryManager", "Failed to create category: Response body is null")
                    }
                } else {
                    Log.e("CategoryManager", "Failed to create category: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CategoryManager", "Error creating category: $e")
            }
        }
    }
}