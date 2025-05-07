package es.timasostima.robank.dto

class CategoryData(
    var id: Long = 0,
    var name: String,
    var color: String
)

data class GoalData(
    var id: Int = 0,
    var name: String,
    var price: Double,
    var index: Int
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