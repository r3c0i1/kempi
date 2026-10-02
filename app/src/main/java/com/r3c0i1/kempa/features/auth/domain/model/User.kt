package com.r3c0i1.kempa.features.auth.domain.model

sealed interface User {
    val id: Long
    val login: String
    val firstName: String
    val lastName: String
    val middleName: String?
    val email: String?

    data class Student (
        override val id: Long,
        override val login: String,
        override val firstName: String,
        override val lastName: String,
        override val middleName: String? = null,
        override val email: String? = null
    ) : User

    data class Teacher (
        override val id: Long,
        override val login: String,
        override val firstName: String,
        override val lastName: String,
        override val middleName: String? = null,
        override val email: String? = null
    ) : User
}
