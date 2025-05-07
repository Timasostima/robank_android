package es.timasostima.robank.database

import android.util.Log
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.dto.BillDTO
import es.timasostima.robank.dto.BillData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.plus

class BillManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val apiClient = RetrofitClient.apiService

    private val _billsState = MutableStateFlow<List<BillData>>(emptyList())
    val billsState: StateFlow<List<BillData>> = _billsState

    init {
        loadBills()
    }

    fun loadBills() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.getBills()
                if (response.isSuccessful) {
                    _billsState.value = response.body() ?: emptyList()
                    Log.i("BillManager", "Bills loaded successfully, categorieID is ${_billsState.value[0].categoryId}")
                } else {
                    Log.e(
                        "BillManager",
                        "Failed to load bills: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("BillManager", "Error loading bills from API: $e")
            }
        }
    }

    fun createBill(bill: BillDTO) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.i("BillManager", bill.toString())
                val response = apiClient.createBill(bill)
                Log.i("BillManager", "response made")
                if (response.isSuccessful) {
                    Log.i("BillManager", "response successful")
                    val createdBill = response.body()
                    if (createdBill != null) {
                        _billsState.value = _billsState.value + createdBill
                    } else {
                        Log.e("BillManager", "Failed to create bill: Response body is null")
                    }
                } else {
                    Log.e(
                        "BillManager",
                        "Failed to create bill: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("BillManager", "Error creating bill: $e")
            }
        }
    }
}