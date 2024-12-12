package es.timasostima.robank.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CategoryData(var name: String, var color: String) {
    constructor(): this("", "")
}

data class BillData(
    var name: String,
    var amount: Double,
    var category: CategoryData,
    var date: String,
    var time: String
){
    constructor(): this("", 0.0, CategoryData(), "", "")
}

class Database (
    private val userId: String
){
    private val db: FirebaseDatabase = Firebase.database("https://robank-5c66d-default-rtdb.europe-west1.firebasedatabase.app")

    fun loadBills(){
        val bills = db.getReference("users/${userId}/bills")
        bills.get().addOnSuccessListener {
            val value = it.value
            println(value)
        }
    }

    fun createUserData(){
        db
            .getReference("users")
            .child(userId)
            .setValue(mapOf("id" to userId))
    }

//    fun updateBills(bills: List<BillData>){
//        db
//            .getReference("users/${userId}/bills")
//            .updateChildren(
//                bills.map {
//                    it.id.toString() to mapOf(
//                        "amount" to it.amount,
//                        "category" to mapOf(
//                            "name" to it.category.name,
//                            "color" to it.category.color
//                        ),
//                        "date" to it.date,
//                        "time" to it.time
//                    )
//                }.toMap()
//            )
//    }

    fun loadCategories(categories: MutableList<CategoryData>){
        db.getReference("users/$userId/categories").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                val value = snapshot.getValue<Map<String, String>>()
                value?.forEach { (name, color) ->
                    categories.add(CategoryData(name, color))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun createCategory(category: CategoryData) {
        db.getReference("users/$userId/categories").child(category.name).setValue(category.color)
    }

    fun loadBills(bills: MutableList<BillData>){
        db.getReference("users/$userId/bills").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bills.clear()
                val value = snapshot.getValue<Map<String, BillData>>()
                value?.forEach { bills.add(it.value) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun createBill(bill: BillData) {
        db.getReference("users/$userId/bills").push().setValue(bill)
    }
}