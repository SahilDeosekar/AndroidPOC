package net.srijan.swiko.user.auth

import app.cash.turbine.test
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.user.auth.GetUserState
import net.srijan.swiko.ui.user.auth.SignInRequest
import net.srijan.swiko.ui.user.auth.UserState
import net.srijan.swiko.ui.user.auth.SignInUiMessages
import net.srijan.swiko.ui.user.auth.UserViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class SignInTest {
    private val username = "abcde@gmail.com"
    private val password = "password"
    private val errorString = "Sign in failed"

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
    fun testItShouldReturnEmailEmptyWhenEmailIsEmpty() = runAndHoldScope {
        val userVM = UserViewModel()
        userVM.uiMessages.test {
            userVM.authenticateUser("", password)
            assertEquals(SignInUiMessages.EmailEmpty,awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnInvalidEmailWhenEmailIsInvalid() = runAndHoldScope {
        val userVM = UserViewModel()
        userVM.uiMessages.test {
            userVM.authenticateUser("abcde@gmm", password)
            assertEquals(SignInUiMessages.EmailInvalid,awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun testItShouldReturnPasswordEmptyWhenPasswordIsEmpty() = runAndHoldScope {
        val userVM = UserViewModel()
        userVM.uiMessages.test {
            userVM.authenticateUser(username, "")
            assertEquals(SignInUiMessages.PasswordEmpty,awaitItem())
            cancel()
        }
        advanceUntilIdle()

    }
    /* @Test
    fun testItShouldReturnUsernameEmptyAndPasswordEmptyWhenUsernameAndPasswordBothAreEmpty() = runAndHoldScope {
        val userVM = UserViewModel()
        userVM.uiMessages.test {
            userVM.authenticateUser("", "")
            assertEquals(SignInUiMessages.EmailEmpty,awaitItem())
            assertEquals(SignInUiMessages.PasswordEmpty,awaitItem())
            cancel()
        }
        advanceUntilIdle()

    } */

    @Test
    fun testItShouldReturnSuccessMessageOnValidUserSignIn() = runAndHoldScope {
        val userVM = UserViewModel()
        async {
            AppStream.messages.test {
                assertEquals(SignInRequest(username, password),awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.SignInSuccess(username,null))
                }
            }
        }

        advanceUntilIdle()

        val job = async{
            userVM.uiMessages.test {
                userVM.authenticateUser(username, password)
                assertEquals(SignInUiMessages.ShowLoading, awaitItem())
                assertEquals(SignInUiMessages.SignInSuccess(username, null),awaitItem())
                assertEquals(SignInUiMessages.HideLoading,awaitItem())
                cancel()
            }
        }

        job.join()
    }

    @Test
    fun testItShouldReturnErrorMessageOnInvalidUserSignIn() = runAndHoldScope {
        val userVM = UserViewModel()
        async {
            AppStream.messages.test {
                assertEquals(SignInRequest(username, password),awaitItem())
                val message = awaitItem()
                if (message is GetUserState) {
                    message.state.complete(UserState.SignInError(errorString))
                }
            }
        }

        advanceUntilIdle()

        val job = async{
            userVM.uiMessages.test {
                userVM.authenticateUser(username, password)
                assertEquals(SignInUiMessages.ShowLoading,awaitItem())
                assertEquals(SignInUiMessages.SignInError(errorString),awaitItem())
                assertEquals(SignInUiMessages.HideLoading,awaitItem())
                cancel()
            }
        }

        job.join()

    }


}