package es.timasostima.robank.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import es.timasostima.robank.BuildConfig
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.dto.BillData
import es.timasostima.robank.dto.CategoryData

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

class Database(
    val userId: String
) {
    private val db: FirebaseDatabase = Firebase.database(BuildConfig.FIREBASE_DATABASE)
    private val client = RetrofitClient.apiService

    fun createUserData() {
        db.getReference("users/$userId/preferences")
            .setValue(
                mapOf(
                    "language" to "system",
                    "currency" to "eur",
                    "theme" to "system",
                    "notifications" to true
                )
            )
    }
    fun loadCategories(categories: MutableList<CategoryData>) {
        db.getReference("users/$userId/categories")
            .addValueEventListener(object : ValueEventListener {
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

    fun loadBills(bills: MutableList<BillData>) {

        db.getReference("users/$userId/bills").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bills.clear()
                val value = snapshot.getValue<Map<String, BillData>>()
                value?.map { bills.add(it.value) }

                bills.sortWith(compareBy(
                    { LocalDate.parse(it.date, dateFormatter) },
                    { LocalTime.parse(it.time, timeFormatter) }
                ))
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