package com.exam.me.model

import com.google.gson.annotations.SerializedName

// This class now matches the backend's actual response for login/register.
data class AuthResponse(
    val user: User,
    @SerializedName("access_token")
    val accessToken: String
)
