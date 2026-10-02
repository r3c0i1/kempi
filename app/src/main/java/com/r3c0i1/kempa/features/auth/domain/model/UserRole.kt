package com.r3c0i1.kempa.features.auth.domain.model

enum class UserRole(
    val title: String
) {
    STUDENT("Студент"),
    TEACHER("Преподаватель"),
    UNKNOWN("Пользователь");

    companion object  {
        fun fromRaw(value: String?): UserRole {
            return when (value?.trim()?.lowercase()) {
                "обучающийся" -> STUDENT
                "сотрудник" -> TEACHER
                else -> UNKNOWN
            }
        }
    }
}