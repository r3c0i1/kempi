package com.r3c0i1.kempa.features.auth.data.mapper

import com.r3c0i1.kempa.features.auth.data.remote.dto.UserInfoDto
import com.r3c0i1.kempa.features.auth.domain.model.User

fun UserInfoDto.toDomainUser(fallbackLogin: String): User {
    val isTeacher = this.userType?.equals("сотрудник", ignoreCase = true) == true

    return if (isTeacher) {
        User.Teacher(
            id = this.id,
            login = this.login.ifBlank { fallbackLogin },
            firstName = this.firstName ?: "",
            lastName = this.lastName ?: "",
            middleName = this.middleName,
            email = this.email
        )
    } else {
        User.Student(
            id = this.id,
            login = this.login.ifBlank { fallbackLogin },
            firstName = this.firstName ?: "",
            lastName = this.lastName ?: "",
            middleName = this.middleName,
            email = this.email
        )
    }
}