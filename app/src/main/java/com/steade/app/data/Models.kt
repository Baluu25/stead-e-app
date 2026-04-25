package com.steade.app.data

data class User(
    val id: Int,
    val name: String,
    val username: String?,
    val email: String,
    val is_admin: Boolean = false
)

data class Habit(
    val id: Int,
    val name: String,
    val description: String?,
    val category: String?,
    val frequency: String,
    val scheduled_days: List<Int>?,
    val target_count: Int,
    val unit: String?,
    val icon: String?,
    val is_active: Boolean,
    val goal_id: Int?,
    val goal_name: String?,
    val completed_today: Int = 0
)

data class Goal(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String?,
    val target_value: Float,
    val current_value: Float,
    val unit: String?,
    val deadline: String?,
    val status: String,
    val progress: Int = 0,
    val habits: List<Habit>? = null
)

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String?,
    val achievement_type: String,
    val threshold_value: Int,
    val progress: Int,
    val unlocked_at: String?
)

data class AuthResponse(
    val user: User,
    val token: String
)

data class HomeData(
    val todays_habits: List<HabitDayItem>?,
    val completed_today_ids: List<Int>?,
    val streak_days: List<StreakDay>?,
    val current_streak: Int,
    val longest_streak: Int
)

data class HabitDayItem(
    val id: Int,
    val name: String,
    val icon: String?,
    val category: String?,
    val unit: String?,
    val target_count: Int,
    val completed: Int,
    val percent: Int,
    val is_done: Boolean
)

data class StreakDay(
    val label: String,
    val completed: Boolean
)

data class Statistics(
    val total_habits: Int,
    val active_habits: Int,
    val current_streak: Int,
    val longest_streak: Int,
    val completions_this_week: Int,
    val daily_completions: Map<String, Int>,
    val category_breakdown: Map<String, Int>
)

data class ProgressResponse(
    val current_value: Float,
    val status: String,
    val progress: Int
)
