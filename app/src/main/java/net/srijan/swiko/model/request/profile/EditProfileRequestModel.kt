package net.srijan.swiko.model.request.profile

import com.google.gson.annotations.SerializedName

data class EditProfileRequestModel(
    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("authToken")
    val authToken: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("dob")
    val dob: String,

    @SerializedName("imageURL")
    val imageUrl: String,
)