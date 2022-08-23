package net.srijan.swiko.ui.user.auth

sealed class SignUpUiMessages {
    object FirstNameEmpty: SignUpUiMessages()
    object LastNameEmpty: SignUpUiMessages()
    object DateOfBirthEmpty: SignUpUiMessages()
    object GenderEmpty: SignUpUiMessages()
    object EmailEmpty: SignUpUiMessages()
    object EmailInvalid: SignUpUiMessages()
    object PasswordEmpty: SignUpUiMessages()
    object PasswordMismatch: SignUpUiMessages()
    object ShowLoading: SignUpUiMessages()
    data class SignUpSuccess(val email: String, val userToken: String?): SignUpUiMessages()
    object HideLoading: SignUpUiMessages()
    data class SignUpError(val error: String): SignUpUiMessages()
}