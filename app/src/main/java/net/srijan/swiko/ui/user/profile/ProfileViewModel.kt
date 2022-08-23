package net.srijan.swiko.ui.user.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.convertDate
import net.srijan.swiko.stream.AppStream.send
import net.srijan.swiko.ui.DateFormatConstants
import net.srijan.swiko.ui.user.auth.EditProfileRequest
import net.srijan.swiko.ui.user.auth.GetUserState
import net.srijan.swiko.ui.user.auth.ProfileFetchRequest
import net.srijan.swiko.ui.user.auth.UserState

class ProfileViewModel: ViewModel() {
    private val _uiMessagesFlow: MutableSharedFlow<ProfileUiMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()
    var selectedGender: String = ""
    var imageUrl: String = ""

    suspend fun authenticateForm(firstName: String, lastName: String, birthDate: String,
                                 gender: String, email: String) {
        when {
            firstName.isEmpty() -> {
                _uiMessagesFlow.emit(ProfileUiMessages.FirstNameEmpty)
            }
            lastName.isEmpty() -> {
                _uiMessagesFlow.emit(ProfileUiMessages.LastNameEmpty)
            }
            birthDate.isEmpty() -> {
                _uiMessagesFlow.emit(ProfileUiMessages.DateOfBirthEmpty)
            }
            gender.isEmpty() -> {
                _uiMessagesFlow.emit(ProfileUiMessages.GenderEmpty)
            }
            else -> {
                _uiMessagesFlow.emit(ProfileUiMessages.ShowLoading)
                val dob = birthDate.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                send(EditProfileRequest(firstName, lastName, email, gender, dob, imageUrl))
                getUserState()
            }
        }
    }

    suspend fun fetchProfile() {
        _uiMessagesFlow.emit(ProfileUiMessages.ShowLoading)
        send(ProfileFetchRequest)
        getUserState()
    }

    private suspend fun getUserState() {
        when (val state = fetchUserState()) {
            is UserState.ProfileFetchSuccess -> _uiMessagesFlow.emit(
                ProfileUiMessages.ProfileFetchSuccess(
                    state.user?.swikoUser?.first()
                )
            )
            is UserState.ProfileFetchFailure -> _uiMessagesFlow.emit(
                ProfileUiMessages.ProfileFetchError(
                    state.error
                )
            )
            is UserState.ProfileEditSuccess -> _uiMessagesFlow.emit(
                ProfileUiMessages.ProfileEditSuccess(
                    state.message
                )
            )
            else -> {
                _uiMessagesFlow.emit(ProfileUiMessages.HideLoading)
            }
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