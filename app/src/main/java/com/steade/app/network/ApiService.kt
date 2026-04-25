package com.steade.app.network

import com.steade.app.data.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<ResponseBody>

    @GET("user")
    suspend fun getUser(): Response<User>

    @GET("habits")
    suspend fun getHabits(): Response<List<Habit>>

    @POST("habits")
    suspend fun createHabit(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Habit>

    @PUT("habits/{id}")
    suspend fun updateHabit(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Habit>

    @DELETE("habits/{id}")
    suspend fun deleteHabit(@Path("id") id: Int): Response<ResponseBody>

    @POST("habit-completions")
    suspend fun completeHabit(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ResponseBody>

    @DELETE("habit-completions/{habitId}/today/last")
    suspend fun undoCompletion(@Path("habitId") habitId: Int): Response<ResponseBody>

    @GET("home")
    suspend fun getHome(): Response<HomeData>

    @GET("goals")
    suspend fun getGoals(): Response<List<Goal>>

    @POST("goals")
    suspend fun createGoal(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ResponseBody>

    @PUT("goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ResponseBody>

    @POST("goals/{id}/progress")
    suspend fun logProgress(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ProgressResponse>

    @DELETE("goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Int): Response<ResponseBody>

    @GET("statistics")
    suspend fun getStatistics(): Response<Statistics>

    @GET("achievements")
    suspend fun getAchievements(): Response<List<Achievement>>

    @GET("profile")
    suspend fun getProfile(): Response<User>

    @PUT("profile")
    suspend fun updateProfile(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<User>

    @Multipart
    @POST("profile/picture")
    suspend fun uploadPicture(@Part picture: MultipartBody.Part): Response<ResponseBody>
}
