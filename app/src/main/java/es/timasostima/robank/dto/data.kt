package es.timasostima.robank.dto

class CategoryData(
    var id: Long = 0,
    var name: String,
    var color: String
)

data class GoalData(
    val id: Int,
    val name: String,
    val price: Double,
    val index: Int = 0,
    val imageUrl: String? = null
)

data class BillData(
    var id: Long = 0,
    var name: String,
    var amount: Double,
    var categoryId: Long,
    var date: String,
    var time: String
)

data class PreferencesData(
    var id : Int = 0,
    var language: String,
    var currency: String,
    var theme: String,
    var notifications: Boolean
)