package net.srijan.swiko.user.forgotpassword

import app.cash.turbine.test
import app.cash.turbine.testIn
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.user.forgotpassword.*
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class ForgetPasswordTest {

    private val email = ""
    private val userEmail = "ashish.gupta@srijan.net"
    private val sampleOtp =  "123456"
    private var scope:CoroutineContext? = null

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
    fun testItShouldReturnEmailIsEmpty() = runAndHoldScope {
        val fPViewModel = ForgotPasswordViewModel()
        fPViewModel.uiMessages.test {
            fPViewModel.submitEmail("")
            Assert.assertEquals(ForgotPasswordUiMessages.EmailEmpty,awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun shouldReturnaInvalidEmailIdWhenEmailIsInvalid() = runAndHoldScope {
        val fPViewModel = ForgotPasswordViewModel()
        fPViewModel.uiMessages.test {
            fPViewModel.submitEmail("asdfgmailcom")
            Assert.assertEquals(ForgotPasswordUiMessages.InvalidEmail, awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }

    @Test
    fun shouldReturnSuccessWhenEmailIdIsValid() = runAndHoldScope {

        //Given
        val fPViewModel = ForgotPasswordViewModel()

        val turbineAppStream = AppStream.messages.testIn(this)
        val turbineUi = fPViewModel.uiMessages.testIn(this)


        async{
            assertEquals(GetOtpRequest(userEmail), turbineAppStream.awaitItem())
            val message = turbineAppStream.awaitItem()
            assertTrue(message is GetResetPasswordState)
            (message as GetResetPasswordState).state.complete(ResetPasswordState.OtpSuccess(sampleOtp))
            turbineAppStream.cancel()
        }

        advanceUntilIdle()

        //When
        async {
            fPViewModel.submitEmail(userEmail)
        }

        //Then
        assertEquals(ForgotPasswordUiMessages.ShowLoading, turbineUi.awaitItem())
        assertEquals(ForgotPasswordUiMessages.OtpSuccess(sampleOtp),turbineUi.awaitItem())
        assertEquals(ForgotPasswordUiMessages.HideLoading, turbineUi.awaitItem())
        turbineUi.cancel()
    }

    @Test
    fun shouldReturnSuccessWhenOtpSuccesfullyVerified() = runAndHoldScope {
        val fPViewModel = ForgotPasswordViewModel()

        val turbineAppStream = AppStream.messages.testIn(this)
        val turbineUi = fPViewModel.uiMessages.testIn(this)

        async{
            assertEquals(VerifyOtpRequest(sampleOtp,email), turbineAppStream.awaitItem())
            val message = turbineAppStream.awaitItem()
            assertTrue(message is GetResetPasswordState)
            (message as GetResetPasswordState).state.complete(ResetPasswordState.OtpSuccessfullyVerified(sampleOtp))
            turbineAppStream.cancel()
        }

        advanceUntilIdle()

        //When
        async {
            fPViewModel.verifyOtp(sampleOtp,email)
        }

        //Then
        assertEquals(ForgotPasswordUiMessages.ShowLoading, turbineUi.awaitItem())
        assertEquals(ForgotPasswordUiMessages.OtpVerifiedSuccessfully,turbineUi.awaitItem())
        assertEquals(ForgotPasswordUiMessages.HideLoading, turbineUi.awaitItem())
        turbineUi.cancel()
    }

    @Test
    fun shouldReturnInvalidOtpWhenOtpIsEmpty() = runAndHoldScope {
        val fPViewModel = ForgotPasswordViewModel()
        fPViewModel.uiMessages.test {
            fPViewModel.verifyOtp("",email)
            Assert.assertEquals(ForgotPasswordUiMessages.InvalidOtp,awaitItem())
            cancel()
        }
    }

    @Test
    fun shouldReturnPasswordMismatchWhenSamePasswordReEntered() = runAndHoldScope {
        val fpViewModel = ForgotPasswordViewModel()
        fpViewModel.uiMessages.test {
            fpViewModel.updatePassword("asdf","nasd",email)
            assertEquals(ForgotPasswordUiMessages.PasswordMisMatch,awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }


    @Test
    fun shouldReturnEmptyPasswordWhenPasswordIsEmptyOrNull() = runAndHoldScope {
        val fpViewModel = ForgotPasswordViewModel()
        fpViewModel.uiMessages.test {
            fpViewModel.updatePassword("",null,email)
            assertEquals(ForgotPasswordUiMessages.BothPasswordsAreEmpty,awaitItem())
            cancel()
        }
        advanceUntilIdle()
    }


}