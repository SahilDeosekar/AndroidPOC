package net.srijan.swiko.network

import net.srijan.swiko.model.request.forgotpassword.GetOtpRequestModel
import net.srijan.swiko.model.request.forgotpassword.UpdatePasswordRequestModel
import net.srijan.swiko.model.request.forgotpassword.VerifyUserOtpRequest
import net.srijan.swiko.model.response.forgotpassword.GetOtpResponse
import net.srijan.swiko.model.response.forgotpassword.UpdatePasswordResponse
import net.srijan.swiko.model.response.forgotpassword.VerifyUserOtpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ResetPasswordService {

    @POST("verifyEmail")
    suspend fun getOtp(@Body request: GetOtpRequestModel): GetOtpResponse {
        TODO("Not implemented")
    }

    @POST("verifyUserOtp")
    suspend fun verifyUserOtp(@Body request: VerifyUserOtpRequest): VerifyUserOtpResponse {
        TODO("Not implemented")
    }

    @POST("resetPassword")
    suspend fun updatePassword(@Body request: UpdatePasswordRequestModel): UpdatePasswordResponse {
        TODO("Not implemented")
    }
}