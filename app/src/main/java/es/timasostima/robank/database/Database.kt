package es.timasostima.robank.database

import android.app.LocaleManager
import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryData(var name: String, var color: String) {
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

data class GoalDTO(
    var name: String,
    var price: Double,
    var index: Int
)

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

    fun loadGoals(goals: MutableList<GoalData>) {
        db.getReference("users/$userId/goals").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                goals.clear()
                val value = snapshot.getValue<Map<String, GoalData>>()
                value?.values?.sortedBy { it.index }?.forEach { goals.add(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun loadGoals2(goals: MutableList<GoalData>) {
        CoroutineScope(Dispatchers.IO).launch {
            goals.clear()
            val res = client.getGoals()
            for (i in res.indices) {
                goals.add(GoalData(res[i].id, res[i].name, res[i].price, res[i].index))
            }
        }
    }

//    fun createGoal(goal: GoalData) {
//        db.getReference("users/$userId/goals").push().setValue(goal)
//    }

    fun createGoal2(goal: GoalDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.createGoal(goal)
            } catch (e: Exception) {
                Log.e("Database", "Error deleting a goal", e)
            }
        }
    }

    fun deleteGoal(goal: GoalData) {
        db.getReference("users/$userId/goals").orderByChild("name").equalTo(goal.name).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    it.ref.removeValue()
                }
        }
    }
    fun deleteGoal2(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.deleteGoal(id)
            } catch (e: Exception) {
                Log.e("Database", "Error deleting a goal", e)
            }
        }
    }

    fun updateGoal(oldName: String, newName: String, newPrice: Double) {
        db.getReference("users/$userId/goals").orderByChild("name").equalTo(oldName).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    it.ref.updateChildren(mapOf("name" to newName, "price" to newPrice))
                }
            }.addOnFailureListener { error ->
            Log.w("TAG", "Failed to read value.", error)
        }
    }
    fun updateGoal2(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.updateGoal(id)
            } catch (e: Exception) {
                Log.e("Database", "Error updating a goal", e)
            }
        }
    }
}