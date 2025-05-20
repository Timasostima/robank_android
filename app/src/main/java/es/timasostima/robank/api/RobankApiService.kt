package es.timasostima.robank.api

import es.timasostima.robank.dto.BillDTO
import es.timasostima.robank.dto.BillData
import es.timasostima.robank.dto.CategoryDTO
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.dto.GoalDTO
import es.timasostima.robank.dto.GoalData
import es.timasostima.robank.dto.PreferencesData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

    @Multipart
    @POST("goals/{id}/image")
    suspend fun uploadGoalImage(
        @Path("id") goalId: Int,
        @Part image: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("goals/{id}/image")
    @Streaming
    suspend fun getGoalImage(@Path("id") goalId: Int): ResponseBody


    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryData>>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryDTO): Response<CategoryData>

    @GET("bills")
    suspend fun getBills(): Response<List<BillData>>

    @POST("bills")
    suspend fun createBill(@Body bill: BillDTO): Response<BillData>

    @GET("user/pfp")
    @Streaming
    suspend fun getUserProfileImage(): ResponseBody

    @Multipart
    @POST("user/upload-pfp")
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part): Response<Map<String, String>>

    @POST("user/upload-pfp-firebase")
    suspend fun uploadProfileImageFromUrl(@Body requestBody: Map<String, String>): Response<Map<String, String>>
}
