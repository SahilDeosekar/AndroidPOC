package net.srijan.swiko.model.request.auth

import com.google.gson.annotations.SerializedName

data class SignInRequestModel(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
