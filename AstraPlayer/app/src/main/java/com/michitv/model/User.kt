package com.michitv.model

data class User(
    val name: String,
    val password: String,
    val enabled: Boolean,
    val maxConnections: Int
)
