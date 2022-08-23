package net.srijan.swiko.ui.user.forgotpassword

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.stream.AppStream.send


class ForgotPasswordViewModel : ViewModel(){

    private val _uiMessagesFlow: MutableSharedFlow<ForgotPasswordUiMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()

    suspend fun submitEmail(email: String) {


        if (email.isEmpty()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.EmailEmpty)
        }
        if (email.isNotEmpty() && !email.isValidEmail()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.InvalidEmail)
        }
        if (email.isNotEmpty() && email.isValidEmail() ) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.ShowLoading)
            send(GetOtpRequest(email))
            getResetPasswordState()
        }
    }

    suspend fun verifyOtp(otp: String,email: String) {
        if (otp.isNullOrEmpty()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.InvalidOtp)
        } else {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.ShowLoading)
            send(VerifyOtpRequest(otp,email))
            getResetPasswordState()
        }
    }

    suspend fun updatePassword(newPassword: String?,
                               reEnteredPassword: String?,
                               email: String
    ){
        if (newPassword.isNullOrEmpty() && reEnteredPassword.isNullOrEmpty()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.BothPasswordsAreEmpty)
        } else if(newPassword.isNullOrEmpty()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.NewPasswordIsEmpty)
        } else if (reEnteredPassword.isNullOrEmpty()) {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.ReenteredPasswordIsEmpty)
        } else  if (newPassword != reEnteredPassword){
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.PasswordMisMatch)
        } else {
            _uiMessagesFlow.emit(ForgotPasswordUiMessages.ShowLoading)
            send(UpdatePasswordsRequest(newPassword,reEnteredPassword,email))
            getResetPasswordState()
        }
    }


    private suspend fun getResetPasswordState() {
        when (val state = fetchResetPasswordState()) {
            ResetPasswordState.OtpNotFetched -> {}
            is ResetPasswordState.OtpError -> _uiMessagesFlow.emit(ForgotPasswordUiMessages.OtpFailure(state.error))
            is ResetPasswordState.OtpSuccess -> _uiMessagesFlow.emit(ForgotPasswordUiMessages.OtpSuccess(state.otp))
            is ResetPasswordState.OtpSuccessfullyVerified -> {
                _uiMessagesFlow.emit(ForgotPasswordUiMessages.OtpVerifiedSuccessfully)
            }
            is ResetPasswordState.OtpVerificationFailed -> {
                _uiMessagesFlow.emit(ForgotPasswordUiMessages.OtpVerificationFailed)
            }
            is ResetPasswordState.UpdatePasswordFailed -> {
                _uiMessagesFlow.emit(ForgotPasswordUiMessages.PasswordUpdateFailed(state.error))
            }
            ResetPasswordState.UpdatePasswordSuccess -> {
                _uiMessagesFlow.emit(ForgotPasswordUiMessages.PasswordUpdateSuccess)
            }
        }
        _uiMessagesFlow.emit(ForgotPasswordUiMessages.HideLoading)
    }

    private suspend fun fetchResetPasswordState(): ResetPasswordState {
        val defState = CompletableDeferred<ResetPasswordState>()
        send(GetResetPasswordState(state = defState))
        return defState.await()
    }

    private val emailAddressRegex = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )


    private fun String?.isValidEmail() = this != null
            && this.matches(emailAddressRegex)



}