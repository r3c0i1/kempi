package com.r3c0i1.kempa.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
class TwoFactorRoute(
    val login: String
) {
}

@Serializable
object MainRoute