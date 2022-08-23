package net.srijan.swiko.model.response.forgotpassword

import com.google.gson.annotations.SerializedName

data class VerifyUserOtpResponse(
    @SerializedName("swiko_user")
    val swikoUser: List<SwikoUser>?
){
    data class SwikoUser(
        @SerializedName("email")
        val email: String?,
        @SerializedName("token")
        val token: String?
    )
}
