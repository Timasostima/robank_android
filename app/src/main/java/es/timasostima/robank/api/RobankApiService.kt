package es.timasostima.robank.api

import es.timasostima.robank.dto.GoalDTO
import es.timasostima.robank.dto.GoalData
import es.timasostima.robank.dto.PreferencesData
import retrofit2.Response
import retrofit2.http.*

data class RobankUser(
    val uid: String,
    val email: String,
    val name: String,
    val pictureUrl: String? = null,
    val language: String? = null,
    val currency: String? = null,
    val theme: String? = null,
    val notifications: Boolean? = null
)

interface RobankApiService {
    @POST("user/register")
    suspend fun registerUser(@Body user: RobankUser): Response<Map<String, String>>

    @PATCH("user/preferences")
    suspend fun updatePreferences(@Body updates: Map<String, String>): Response<Map<String, String>>

    @GET("user/preferences")
    suspend fun getPreferences(): PreferencesData


    @GET("goals")
    suspend fun getGoals(): Response<List<GoalData>>

    @POST("goals")
    suspend fun createGoal(@Body goal: GoalDTO): Response<GoalData>

    @PUT("goals/{id}")
    suspend fun updateGoal(@Path("id") id: Int, @Body goal: GoalDTO): Response<Unit>

    @DELETE("goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Int): Response<Unit>
}
