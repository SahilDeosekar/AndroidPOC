package net.srijan.swiko.ui.user.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.isValidEmail
import net.srijan.swiko.stream.AppStream.send

class UserViewModel : ViewModel() {

    private val _uiMessagesFlow: MutableSharedFlow<SignInUiMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()

    suspend fun authenticateUser(email: String, password: String) {
        when {
            email.isEmpty() -> {
                _uiMessagesFlow.emit(SignInUiMessages.EmailEmpty)
            }
            email.isNotEmpty() && !email.isValidEmail() -> {
                _uiMessagesFlow.emit(SignInUiMessages.EmailInvalid)
            }
            password.isEmpty() -> {
                _uiMessagesFlow.emit(SignInUiMessages.PasswordEmpty)
            }
            else -> {
                _uiMessagesFlow.emit(SignInUiMessages.ShowLoading)
                send(SignInRequest(email, password))
                getUserState()
            }
        }
    }

    private suspend fun getUserState() {
        when (val state = fetchUserState()) {
            UserState.NotSignedIn -> {}
            is UserState.SignInError -> _uiMessagesFlow.emit(SignInUiMessages.SignInError(state.error))
            is UserState.SignInSuccess -> _uiMessagesFlow.emit(
                SignInUiMessages.SignInSuccess(
                    state.username,
                    state.userToken
                )
            )
            else -> { _uiMessagesFlow.emit(SignInUiMessages.HideLoading) }
        }

    }

    private suspend fun fetchUserState(): UserState {
        val defState = CompletableDeferred<UserState>()
        send(GetUserState(state = defState))
        return defState.await()
    }
}