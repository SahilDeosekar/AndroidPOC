package net.srijan.swiko.user.profile

import app.cash.turbine.test
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.convertDate
import net.srijan.swiko.model.response.profile.GetUserResponseModel
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.DateFormatConstants
import net.srijan.swiko.ui.user.auth.EditProfileRequest
import net.srijan.swiko.ui.user.auth.GetUserState
import net.srijan.swiko.ui.user.auth.ProfileFetchRequest
import net.srijan.swiko.ui.user.auth.UserState
import net.srijan.swiko.ui.user.profile.ProfileUiMessages
import net.srijan.swiko.ui.user.profile.ProfileViewModel
import org.junit.After
import org.junit.Assert
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class ProfileTest {
    private val firstName = "Test"
    private val lastName = "User"
    private val dateOfBirth = "16/05/1997"
    private val gender: String = "Male"
    private val email: String = "test.user@gmail.com"
    private val sampleAuthToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFzQGdtYWlsLmNvbSIsImlhdCI6MTY1MzQwNjIwMH0.h08QTaqjC6fa9cFTBvTH9GdNZIhpn8ElKAVJaCmtDL0"
    private val imageUrl = ""
    private val profileFetchErrorMessage: String = "Failed to fetch user profile!!!"
    private val profileEditSuccessMessage: String = "Profile Updated Successfully!!!"
    private val profileEditErrorMessage: String = "Failed to edit user profile!!!"

    private var scope: CoroutineContext? = null

    @After
    fun reset(){
        scope?.cancel()
        scope = null
    }

    fun runAndHoldScope(testBody: suspend TestScope.() -> Unit){
        scope = EmptyCoroutineContext + Job()
        runTest(context = scope!!, testBody = testBody)
        scope?.cancel()
        scope = null
    }

    @Test
    fun testItShouldReturnEmptyFirstNameWhenFirstNameIsEmpty() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        profileVM.uiMessages.test {
            profileVM.authenticateForm("", lastName, dateOfBirth, gender, email)
            assertEquals(ProfileUiMessages.FirstNameEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnEmptyLastNameWhenLastNameIsEmpty() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        profileVM.uiMessages.test {
            profileVM.authenticateForm(firstName, "", dateOfBirth, gender, email)
            assertEquals(ProfileUiMessages.LastNameEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnEmptyDateOfBirthWhenDateOfBirthIsEmpty() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        profileVM.uiMessages.test {
            profileVM.authenticateForm(firstName, lastName, "", gender, email)
            assertEquals(ProfileUiMessages.DateOfBirthEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnEmptyGenderWhenGenderIsEmpty() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        profileVM.uiMessages.test {
            profileVM.authenticateForm(firstName, lastName, dateOfBirth, "", email)
            assertEquals(ProfileUiMessages.GenderEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnSuccessOnSuccessfulProfileFetch() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        val swikoUser: GetUserResponseModel.SwikoUser? = GetUserResponseModel.SwikoUser(sampleAuthToken, dateOfBirth, email, firstName, gender, 1, imageUrl, lastName)
        val userResponse: GetUserResponseModel = GetUserResponseModel(swikoUser = listOf(swikoUser))

       launch {
            AppStream.messages.test {
                assertEquals(ProfileFetchRequest, awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.ProfileFetchSuccess(userResponse))
                }
            }
        }
        advanceUntilIdle()
        profileVM.uiMessages.test {
            profileVM.fetchProfile()
            assertEquals(ProfileUiMessages.ShowLoading, awaitItem())
            assertEquals(ProfileUiMessages.ProfileFetchSuccess(swikoUser), awaitItem())
            assertEquals(ProfileUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }

    @Test
    fun testItShouldReturnFailureOnFailedProfileFetch() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        launch {
            AppStream.messages.test {
                assertEquals(ProfileFetchRequest, awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.ProfileFetchFailure(profileFetchErrorMessage))
                }
            }
        }
        advanceUntilIdle()
        profileVM.uiMessages.test {
            profileVM.fetchProfile()
            assertEquals(ProfileUiMessages.ShowLoading, awaitItem())
            assertEquals(ProfileUiMessages.ProfileFetchError(profileFetchErrorMessage), awaitItem())
            assertEquals(ProfileUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }

    @Test
    fun testItShouldReturnSuccessOnSuccessfulProfileUpdate() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        launch {
            AppStream.messages.test {
                val formattedDate = dateOfBirth.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                Assert.assertEquals(EditProfileRequest(firstName, lastName, email, gender, formattedDate, imageUrl), awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.ProfileEditSuccess(profileEditSuccessMessage))
                }
            }
        }
        advanceUntilIdle()
        profileVM.uiMessages.test {
            profileVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email)
            Assert.assertEquals(ProfileUiMessages.ShowLoading, awaitItem())
            Assert.assertEquals(ProfileUiMessages.ProfileEditSuccess(profileEditSuccessMessage), awaitItem())
            Assert.assertEquals(ProfileUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }

    @Test
    fun testItShouldReturnErrorOnFailedProfileEdit() = runAndHoldScope {
        val profileVM = ProfileViewModel()
        launch {
            AppStream.messages.test {
                val formattedDate = dateOfBirth.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                Assert.assertEquals(EditProfileRequest(firstName, lastName, email, gender, formattedDate, imageUrl), awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.ProfileFetchFailure(profileEditErrorMessage))
                }
            }
        }
        advanceUntilIdle()
        profileVM.uiMessages.test {
            profileVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email)
            Assert.assertEquals(ProfileUiMessages.ShowLoading, awaitItem())
            Assert.assertEquals(ProfileUiMessages.ProfileFetchError(profileEditErrorMessage), awaitItem())
            Assert.assertEquals(ProfileUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }

}