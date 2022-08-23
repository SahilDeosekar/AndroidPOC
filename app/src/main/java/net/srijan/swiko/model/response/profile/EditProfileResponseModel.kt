package net.srijan.swiko.model.response.profile


import com.google.gson.annotations.SerializedName

data class EditProfileResponseModel(
    @SerializedName("update_swiko_user")
    val updateSwikoUser: UpdateSwikoUser?
) {
    data class UpdateSwikoUser(
        @SerializedName("returning")
        val returning: List<User?>?
    ) {
        data class User(
            @SerializedName("dob")
            val dob: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("gender")
            val gender: String?,
            @SerializedName("image_url")
            val imageUrl: String?,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("token")
            val token: Any?
        )
    }
}