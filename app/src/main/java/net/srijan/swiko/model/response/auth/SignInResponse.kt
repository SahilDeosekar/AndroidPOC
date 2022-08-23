package net.srijan.swiko.model.response.auth


import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("login")
    val login: Login?
) {
    data class Login(
        @SerializedName("auth_token")
        val authToken: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("last_name")
        val lastName: String?
    )
}