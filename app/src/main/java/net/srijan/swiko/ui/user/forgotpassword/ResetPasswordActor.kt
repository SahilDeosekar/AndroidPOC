package net.srijan.swiko.ui.user.forgotpassword

import kotlinx.coroutines.CompletableDeferred
import net.srijan.swiko.model.request.forgotpassword.GetOtpRequestModel
import net.srijan.swiko.model.request.forgotpassword.UpdatePasswordRequestModel
import net.srijan.swiko.model.request.forgotpassword.VerifyUserOtpRequest
import net.srijan.swiko.network.ResetPasswordService
import net.srijan.swiko.stream.Message
import net.srijan.swiko.stream.State
import net.srijan.swiko.ui.actor.ActorFunction
import net.srijan.swiko.ui.actor.TheActorScope
import java.lang.Exception


data class GetOtpRequest(val email: String) : Message
data class VerifyOtpRequest(val otp: String,val email: String) : Message
data class UpdatePasswordsRequest(val newPassword: String,
                                  val reEnteredPassword: String,
                                  val email: String) : Message

data class GetResetPasswordState(val state: CompletableDeferred<ResetPasswordState> = CompletableDeferred()) : Message

sealed class ResetPasswordState : State {
    data class OtpSuccess(val otp: String) : ResetPasswordState()
    data class OtpError(val error: String) : ResetPasswordState()
    object OtpNotFetched : ResetPasswordState()

    data class OtpSuccessfullyVerified(val otp: String): ResetPasswordState()
    data class OtpVerificationFailed(val error: String): ResetPasswordState()

    object UpdatePasswordSuccess: ResetPasswordState()
    data class UpdatePasswordFailed(val error: String): ResetPasswordState()
}

fun resetPasswordActor(
    resetPasswordService: ResetPasswordService
): ActorFunction<ResetPasswordState>{

    suspend fun getOtp(email: String): ResetPasswordState {
        return try {

            val getOtpRequestModel = GetOtpRequestModel(email)
            val getOtpResponse = resetPasswordService.getOtp(getOtpRequestModel)

            if(getOtpResponse.forgotPassword?.success == true) {
                ResetPasswordState.OtpSuccess("")
            } else {
                ResetPasswordState.OtpError("Unable to fetch the otp")
            }
        } catch (e: Exception) {
            ResetPasswordState.OtpError("Unable to fetch the otp")
        }
    }

    suspend fun verifyOtp(email: String, otp: String): ResetPasswordState {
        return try {

            val verifyUserOtpRequest = VerifyUserOtpRequest(email=email, token =otp)
            val verifyUserOtpResponse = resetPasswordService.verifyUserOtp(verifyUserOtpRequest)

            if(!verifyUserOtpResponse.swikoUser?.firstOrNull()?.email.isNullOrEmpty()) {
                ResetPasswordState.OtpSuccessfullyVerified("")
            } else {
                ResetPasswordState.OtpVerificationFailed("Please check the otp you have entered")
            }
        } catch (e: Exception) {
            ResetPasswordState.OtpVerificationFailed("Please check the otp you have entered")
        }
    }

    suspend fun updatePasswords(newPassword: String,
                                reEnteredPassword: String,
                                email: String
    ): ResetPasswordState{
        return try {

            val updatePasswordRequestModel = UpdatePasswordRequestModel(
                email,
                newPassword
            )

           val updatePasswordResponse =  resetPasswordService.updatePassword(updatePasswordRequestModel)

            if (updatePasswordResponse.updateSwikoUser?.returning?.firstOrNull()?.email == email) {
                ResetPasswordState.UpdatePasswordSuccess
            } else {
                ResetPasswordState.UpdatePasswordFailed("Something went wrong. Please try again.")
            }

        } catch (e: Exception) {
            ResetPasswordState.UpdatePasswordFailed("Something went wrong. Please try again.")
        }
    }

    tailrec suspend fun TheActorScope.function(state: ResetPasswordState) {
        val message = channel.receive()
        when(message) {
            is GetOtpRequest -> {
                val user  = getOtp(message.email)
                function(user)
            }


            is VerifyOtpRequest -> {
                val bool = verifyOtp(message.email,message.otp)
                function(bool)
            }

            is GetResetPasswordState -> {
                message.state.complete(state)
                function(state)
            }

            is UpdatePasswordsRequest -> {
                val bool = updatePasswords(
                    message.newPassword,
                    message.reEnteredPassword,
                    message.email
                )
                function(bool)
            }

            else -> function(state)

        }
    }

    return TheActorScope::function

}