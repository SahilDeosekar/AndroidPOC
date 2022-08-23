package net.srijan.swiko.model.request.auth

import com.google.gson.annotations.SerializedName


data class SignUpRequestModel(
    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("dob")
    val dob: String,

    @SerializedName("image_url")
    val imageUrl: String,
)
