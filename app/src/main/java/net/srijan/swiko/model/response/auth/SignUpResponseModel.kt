package net.srijan.swiko.model.response.auth


import com.google.gson.annotations.SerializedName

data class SignUpResponseModel(
    @SerializedName("registerUser")
    val registerUser: RegisterUser?
) {
    data class RegisterUser(
        @SerializedName("authToken")
        val authToken: String?,
        @SerializedName("dob")
        val dob: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("firstName")
        val firstName: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("image_url")
        val imageUrl: Any?,
        @SerializedName("lastName")
        val lastName: String?
    )
}