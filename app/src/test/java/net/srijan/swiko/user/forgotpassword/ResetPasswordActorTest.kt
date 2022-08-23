package net.srijan.swiko.user.forgotpassword

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.model.request.forgotpassword.GetOtpRequestModel
import net.srijan.swiko.model.response.forgotpassword.GetOtpResponse
import net.srijan.swiko.network.ResetPasswordService
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.actor.TheActor
import net.srijan.swiko.ui.user.forgotpassword.GetOtpRequest
import net.srijan.swiko.ui.user.forgotpassword.GetResetPasswordState
import net.srijan.swiko.ui.user.forgotpassword.ResetPasswordState
import net.srijan.swiko.ui.user.forgotpassword.resetPasswordActor
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class ResetPasswordActorTest {

    @Test
    fun itShouldReturnTheState() = runTest{

        val resetPasswordService = object:ResetPasswordService{

        }

        val actor = TheActor.run {
            resetPasswordActor(resetPasswordService).toActor(ResetPasswordState.OtpNotFetched)
        }

        val job = actor.start(this)
        advanceUntilIdle()


        //When
        val stateResponse = CompletableDeferred<ResetPasswordState>()
        AppStream.send(GetResetPasswordState(stateResponse))

        Assert.assertEquals(ResetPasswordState.OtpNotFetched, stateResponse.await())
        job.cancel()

    }

    @Test
    fun itShouldReturnReturnTheStateWhichTheActorCurrentlyHolds() = runTest{

        val resetPasswordService = object:ResetPasswordService{

        }

        val actor = TheActor.run {
            resetPasswordActor(resetPasswordService).toActor(ResetPasswordState.UpdatePasswordSuccess)
        }
        val job = actor.start(this)

        advanceUntilIdle()

        //When
        val stateResponse = CompletableDeferred<ResetPasswordState>()
        AppStream.send(GetResetPasswordState(stateResponse))

        Assert.assertEquals(ResetPasswordState.UpdatePasswordSuccess, stateResponse.await())
        job.cancel()

    }

    @Test
    fun itShouldBeInTheStateOfOtpSuccessIfOtpHasBeenSuccessfullyRequested() = runTest {
        val resetPasswordService = object:ResetPasswordService{
            override suspend fun getOtp(request: GetOtpRequestModel): GetOtpResponse {
                return GetOtpResponse(forgotPassword = GetOtpResponse.ForgotPassword(success = true))
            }
        }

        val actor = TheActor.run {
            resetPasswordActor(resetPasswordService).toActor(ResetPasswordState.OtpNotFetched)
        }

        val job = actor.start(this)

        advanceUntilIdle()

        //When
        AppStream.send(GetOtpRequest("someEmail@someDomain.com"))


        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(ResetPasswordState.OtpSuccess(""), currentState)
        job.cancel()
    }

    private suspend fun getCurrentStateOfActor(): ResetPasswordState {
        val stateResponse = CompletableDeferred<ResetPasswordState>()
        AppStream.send(GetResetPasswordState(stateResponse))
        val currentState = stateResponse.await()
        return currentState
    }


}