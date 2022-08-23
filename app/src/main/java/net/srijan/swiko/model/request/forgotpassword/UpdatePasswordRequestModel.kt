package net.srijan.swiko.model.request.forgotpassword

import com.google.gson.annotations.SerializedName

data class UpdatePasswordRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
