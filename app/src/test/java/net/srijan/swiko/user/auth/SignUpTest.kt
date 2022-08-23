package net.srijan.swiko.user.auth

import app.cash.turbine.test
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.convertDate
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.DateFormatConstants
import net.srijan.swiko.ui.user.auth.GetUserState
import net.srijan.swiko.ui.user.auth.SignUpRequest
import net.srijan.swiko.ui.user.auth.UserState
import net.srijan.swiko.ui.user.auth.SignUpUiMessages
import net.srijan.swiko.ui.user.auth.SignUpViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SignUpTest {
    private val firstName: String = "Test"
    private val lastName: String = "User"
    private val dateOfBirth: String = "16/05/1997"
    private val gender: String = "Male"
    private val email: String = "testuser123@gmail.com"
    private val password: String = "test@123"
    private val signUpErrorMessage: String = "Sign Up Failed!!!"

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
    fun testItShouldReturnFirstNameEmptyWhenFirstNameIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm("", lastName, dateOfBirth, gender, email, password, password)
            assertEquals(SignUpUiMessages.FirstNameEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnLastNameEmptyWhenLastNameIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, "", dateOfBirth, gender, email, password, password)
            assertEquals(SignUpUiMessages.LastNameEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnDateOfBirthEmptyWhenDateOfBirthIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, "", gender, email, password, password)
            assertEquals(SignUpUiMessages.DateOfBirthEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnGenderEmptyWhenGenderIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, "", email, password, password)
            assertEquals(SignUpUiMessages.GenderEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnEmailEmptyWhenEmailIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, "", password, password)
            assertEquals(SignUpUiMessages.EmailEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnInvalidEmailWhenEmailIsInvalid() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, "abc@ggg", password, password)
            assertEquals(SignUpUiMessages.EmailInvalid, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnPasswordEmptyWhenPasswordIsEmpty() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email, "", "")
            assertEquals(SignUpUiMessages.PasswordEmpty, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnPasswordMismatchWhenPasswordsDoNotMatch() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email, password, "")
            assertEquals(SignUpUiMessages.PasswordMismatch, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnSuccessOnValidUserSignUp() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        launch {
            AppStream.messages.test {
                val formattedDate = dateOfBirth.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                assertEquals(SignUpRequest(firstName, lastName, email, password, gender, formattedDate, ""), awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.SignInSuccess(email,null))
                }
            }
        }
        advanceUntilIdle()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email, password, password)
            assertEquals(SignUpUiMessages.ShowLoading, awaitItem())
            assertEquals(SignUpUiMessages.SignUpSuccess(email, null), awaitItem())
            assertEquals(SignUpUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }

    @Test
    fun testItShouldReturnErrorOnDuplicateUserSignUp() = runAndHoldScope {
        val signUpVM = SignUpViewModel()
        launch {
            AppStream.messages.test {
                val formattedDate = dateOfBirth.convertDate(DateFormatConstants.yearMonthDay, DateFormatConstants.formatTnZLong)
                assertEquals(SignUpRequest(firstName, lastName, email, password, gender, formattedDate, ""), awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.SignInError(signUpErrorMessage))
                }
            }
        }
        advanceUntilIdle()
        signUpVM.uiMessages.test {
            signUpVM.authenticateForm(firstName, lastName, dateOfBirth, gender, email, password, password)
            assertEquals(SignUpUiMessages.ShowLoading, awaitItem())
            assertEquals(SignUpUiMessages.SignUpError(signUpErrorMessage), awaitItem())
            assertEquals(SignUpUiMessages.HideLoading, awaitItem())
            cancel()
        }
    }
}