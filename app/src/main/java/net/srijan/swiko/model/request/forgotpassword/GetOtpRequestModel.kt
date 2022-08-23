package net.srijan.swiko.model.request.forgotpassword

import com.google.gson.annotations.SerializedName

data class GetOtpRequestModel(
    @SerializedName("email")
    val email: String,
)
