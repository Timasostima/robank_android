package es.timasostima.robank.dto

class CategoryData(
    var name: String,
    var color: String
) {
    constructor() : this("", "")
}

data class GoalData(
    var id: Int = 0,
    var name: String,
    var price: Double,
    var index: Int
) {
    constructor() : this(0, "", 0.0, 0)
}

data class BillData(
    var name: String,
    var amount: Double,
    var category: CategoryData,
    var date: String,
    var time: String
) {
    constructor() : this("", 0.0, CategoryData(), "", "")
}

data class PreferencesData(
    var id : Int = 0,
    var language: String,
    var currency: String,
    var theme: String,
    var notifications: Boolean
) {
    constructor() : this(0, "", "", "", false)
}