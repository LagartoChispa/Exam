package com.exam.me.model

// Simplified DTO for user registration
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val role: String = "USUARIO" // Default role as per backend guide
)
