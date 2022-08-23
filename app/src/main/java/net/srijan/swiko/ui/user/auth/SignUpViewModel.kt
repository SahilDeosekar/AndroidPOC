package net.srijan.swiko.ui.user.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.convertDate
import net.srijan.swiko.isValidEmail
import net.srijan.swiko.stream.AppStream.send
import net.srijan.swiko.ui.DateFormatConstants

class SignUpViewModel: ViewModel() {
    private val _uiMessagesFlow: MutableSharedFlow<SignUpUiMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()
    var selectedGender: String = ""
    var imageUrl: String = ""

    suspend fun authenticateForm(firstName: String, lastName: String, birthdate: String,
                                 gender: String, email: String, password: String,
                                 confirmPassword: String) {
        when {
            firstName.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.FirstNameEmpty)
            }
            lastName.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.LastNameEmpty)
            }
            birthdate.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.DateOfBirthEmpty)
            }
            gender.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.GenderEmpty)
            }
            email.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.EmailEmpty)
            }
            email.isNotEmpty() && !email.isValidEmail() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.EmailInvalid)
            }
            password.isEmpty() -> {
                _uiMessagesFlow.emit(SignUpUiMessages.PasswordEmpty)
            }
            !password.equals(confirmPassword) -> {
                _uiMessagesFlow.emit(SignUpUiMessages.PasswordMismatch)
            }
            else -> {
                _uiMessagesFlow.emit(SignUpUiMessages.ShowLoading)
                val dob = birthdate.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                send(SignUpRequest(firstName, lastName, email, password, gender, dob, imageUrl))
                getUserState()
            }
        }
    }

    private suspend fun getUserState() {
        when (val state = fetchUserState()) {
            UserState.NotSignedIn -> {}
            is UserState.SignInSuccess -> _uiMessagesFlow.emit(
                SignUpUiMessages.SignUpSuccess(
                    state.username,
                    state.userToken
                )
            )
            is UserState.SignInError -> _uiMessagesFlow.emit(SignUpUiMessages.SignUpError(state.error))
            else -> { _uiMessagesFlow.emit(SignUpUiMessages.HideLoading) }
        }

    }

    private suspend fun fetchUserState(): UserState {
        val defState = CompletableDeferred<UserState>()
        send(GetUserState(state = defState))
        return defState.await()
    }

    fun updateGender(gender: String) {
        selectedGender = gender
    }

    fun updateImageUrl(url: String) {
        imageUrl = url
    }
}