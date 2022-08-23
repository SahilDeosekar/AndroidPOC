package net.srijan.swiko.model.response.profile


import com.google.gson.annotations.SerializedName

data class GetUserResponseModel(
    @SerializedName("swiko_user")
    val swikoUser: List<SwikoUser?>?
) {
    data class SwikoUser(
        @SerializedName("authToken")
        val authToken: String?,
        @SerializedName("dob")
        val dob: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("image_url")
        val imageUrl: String?,
        @SerializedName("last_name")
        val lastName: String?
    )
}