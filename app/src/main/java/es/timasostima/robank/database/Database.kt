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
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CategoryData(var name: String, var color: String) {
    constructor() : this("", "")
}

data class GoalData(
    var name: String,
    var price: Double,
    var index: Int
) {
    constructor() : this("", 0.0, 0)
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
    var language: String,
    var currency: String,
    var theme: String,
    var notifications: Boolean
) {
    constructor() : this("", "", "", false)
}

val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

class Database(
    private val userId: String
) {
    private val db: FirebaseDatabase =
        Firebase.database("https://robank-5c66d-default-rtdb.europe-west1.firebasedatabase.app")

    fun createUserData() {
        db
            .getReference("users/$userId/preferences")
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

    fun createGoal(goal: GoalData) {
        db.getReference("users/$userId/goals").push().setValue(goal)
    }

    fun deleteGoal(goal: GoalData) {
        db.getReference("users/$userId/goals").orderByChild("name").equalTo(goal.name).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    it.ref.removeValue()
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

    fun loadPreferences(
        preferences: PreferencesData,
        changeMode: (Boolean?) -> Unit,
        context: Context
    ) {
        db.getReference("users/$userId/preferences")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue<PreferencesData>()
                    if (value != null) {
                        preferences.language = value.language
                        preferences.currency = value.currency
                        preferences.theme = value.theme
                        preferences.notifications = value.notifications

                        changeMode(
                            when (preferences.theme) {
                                "system" -> null
                                "dark" -> true
                                "light" -> false
                                else -> null
                            }
                        )
                        context
                            .getSystemService(LocaleManager::class.java)
                            .applicationLocales =
                            android.os.LocaleList(
                                java.util.Locale(
                                    value.language.lowercase(),
                                    value.language.uppercase()
                                )
                            )
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
    }

    fun changeTheme(theme: String) {
        db.getReference("users/$userId/preferences/theme").setValue(theme)
    }

    fun changeLanguage(language: String) {
        db.getReference("users/$userId/preferences/language").setValue(language)
    }

    fun changeCurrency(currency: String) {
        db.getReference("users/$userId/preferences/currency").setValue(currency)
    }

    fun changeNotifications(permitted: Boolean) {
        db.getReference("users/$userId/preferences/notifications").setValue(permitted)
    }
}