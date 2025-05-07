package es.timasostima.robank.dto

class BillDTO(
    var name: String,
    var amount: Double,
    var categoryId: Long,
    var date: String,
    var time: String
) {
    override fun toString(): String {
        return "BillDTO(name='$name', amount=$amount, categoryId=$categoryId, date='$date', time='$time')"
    }
}