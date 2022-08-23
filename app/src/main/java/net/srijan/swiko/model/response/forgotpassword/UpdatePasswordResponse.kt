package net.srijan.swiko.model.response.forgotpassword


import com.google.gson.annotations.SerializedName

data class UpdatePasswordResponse(
    @SerializedName("update_swiko_user")
    val updateSwikoUser: UpdateSwikoUser?
) {
    data class UpdateSwikoUser(
        @SerializedName("returning")
        val returning: List<Returning>?
    )
    data class Returning(
        @SerializedName("id")
        val id: String?,
        @SerializedName("email")
        val email: String?,

    )
}