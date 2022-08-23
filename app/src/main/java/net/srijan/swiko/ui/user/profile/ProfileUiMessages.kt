package net.srijan.swiko.ui.user.profile

import net.srijan.swiko.model.response.profile.GetUserResponseModel

sealed class ProfileUiMessages {
    object FirstNameEmpty: ProfileUiMessages()
    object LastNameEmpty: ProfileUiMessages()
    object DateOfBirthEmpty: ProfileUiMessages()
    object GenderEmpty: ProfileUiMessages()
    object ShowLoading: ProfileUiMessages()
    data class ProfileFetchSuccess(val profile: GetUserResponseModel.SwikoUser?): ProfileUiMessages()
    object HideLoading: ProfileUiMessages()
    data class ProfileFetchError(val error: String): ProfileUiMessages()
    data class ProfileEditSuccess(val message: String): ProfileUiMessages()
}