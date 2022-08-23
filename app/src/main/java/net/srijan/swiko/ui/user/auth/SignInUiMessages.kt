package net.srijan.swiko.ui.user.auth

sealed class SignInUiMessages {
    object EmailEmpty: SignInUiMessages()
    object EmailInvalid: SignInUiMessages()
    object PasswordEmpty: SignInUiMessages()
    object ShowLoading: SignInUiMessages()
    object HideLoading: SignInUiMessages()
    data class SignInSuccess(val username:String, val userToken: String?): SignInUiMessages()
    data class SignInError(val error:String): SignInUiMessages()
}
