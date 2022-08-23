package net.srijan.swiko.ui.user.forgotpassword

sealed class ForgotPasswordUiMessages {
    object EmailEmpty: ForgotPasswordUiMessages()
    object ShowLoading:ForgotPasswordUiMessages()
    object HideLoading:ForgotPasswordUiMessages()
    data class OtpSuccess(val otp:String): ForgotPasswordUiMessages()
    data class OtpFailure(val error:String): ForgotPasswordUiMessages()
    object InvalidEmail: ForgotPasswordUiMessages()

    object InvalidOtp: ForgotPasswordUiMessages()
    object OtpVerifiedSuccessfully: ForgotPasswordUiMessages()
    object OtpVerificationFailed: ForgotPasswordUiMessages()

    object  PasswordUpdateSuccess: ForgotPasswordUiMessages()
    data class PasswordUpdateFailed(val error: String): ForgotPasswordUiMessages()
    object PasswordMisMatch: ForgotPasswordUiMessages()

    object NewPasswordIsEmpty: ForgotPasswordUiMessages()
    object ReenteredPasswordIsEmpty: ForgotPasswordUiMessages()
    object BothPasswordsAreEmpty: ForgotPasswordUiMessages()
}
