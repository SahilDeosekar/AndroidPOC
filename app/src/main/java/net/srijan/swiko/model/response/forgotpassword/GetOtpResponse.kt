package net.srijan.swiko.model.response.forgotpassword


import com.google.gson.annotations.SerializedName

data class GetOtpResponse(
    @SerializedName("forgotPassword")
    val forgotPassword: ForgotPassword?
) {
    data class ForgotPassword(
        @SerializedName("success")
        val success: Boolean?
    )
}