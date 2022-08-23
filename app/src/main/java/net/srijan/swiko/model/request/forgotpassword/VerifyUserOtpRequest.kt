package net.srijan.swiko.model.request.forgotpassword

import com.google.gson.annotations.SerializedName

data class VerifyUserOtpRequest (

        @SerializedName("email")
        val email: String,
        @SerializedName("token")
        val token: String,
)