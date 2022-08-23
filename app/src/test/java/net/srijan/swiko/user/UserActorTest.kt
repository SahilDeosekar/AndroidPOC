package net.srijan.swiko.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.*
import net.srijan.swiko.AppConfig
import net.srijan.swiko.model.request.auth.SignInRequestModel
import net.srijan.swiko.model.request.auth.SignUpRequestModel
import net.srijan.swiko.model.request.profile.EditProfileRequestModel
import net.srijan.swiko.model.response.auth.SignInResponse
import net.srijan.swiko.model.response.auth.SignUpResponseModel
import net.srijan.swiko.model.response.profile.EditProfileResponseModel
import net.srijan.swiko.model.response.profile.GetUserResponseModel
import net.srijan.swiko.network.ResetPasswordService
import net.srijan.swiko.network.UserService
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.actor.TheActor
import net.srijan.swiko.ui.actor.TheActor.Companion.toActor
import net.srijan.swiko.ui.user.LocalisationProvider
import net.srijan.swiko.ui.user.auth.*
import net.srijan.swiko.ui.user.forgotpassword.GetResetPasswordState
import net.srijan.swiko.ui.user.forgotpassword.ResetPasswordState
import net.srijan.swiko.ui.user.forgotpassword.resetPasswordActor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class UserActorTest {

    private val firstName = "Test"
    private val sampleAuthToken = "sampleToken"
    private val lastName = "User"
    private val dateOfBirth = "16/05/1997"
    private val gender: String = "Male"
    private val email: String = "kartik.saraf@srijan.net"
    private val password = "test@321"
    private val imageUrl = ""


    private lateinit var tokenCache: TokenCache

    @Before
    fun init(){
        tokenCache = object:TokenCache{

            var currentToken:String? = null

            override suspend fun readToken(): String? = currentToken

            override suspend fun saveToken(token: String) {
                currentToken = token
            }

        }
    }


    @Test
    fun itShouldReturnState() = runTest {
        val userService = object : UserService {}
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()


        //When
        val stateResponse = CompletableDeferred<UserState>()
        AppStream.send(GetUserState(stateResponse))

        Assert.assertEquals(UserState.NotSignedIn, stateResponse.await())
        job.cancel()
    }

    @Test
    fun itShouldReturnReturnTheStateWhichTheActorCurrentlyHolds() = runTest{

        val userService = object : UserService {}
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.SignInError(""))
        }
        val job = actor.start(this)

        advanceUntilIdle()

        //When
        val stateResponse = CompletableDeferred<UserState>()
        AppStream.send(GetUserState(stateResponse))

        Assert.assertEquals(UserState.SignInError(""), stateResponse.await())
        job.cancel()

    }

    @Test
    fun itShouldReturnStateSignInSuccessWhenUserHasSuccessfullySignedIn() = runTest {
        val userService = object: UserService {
            override suspend fun signInUser(request: SignInRequestModel): SignInResponse {
                return SignInResponse(login = SignInResponse.Login(sampleAuthToken, email, firstName, 1, lastName))
            }
        }
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()

        //When
        AppStream.send(SignInRequest(email, password))

        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(UserState.SignInSuccess(email, sampleAuthToken), currentState)
        job.cancel()
    }

    @Test
    fun itShouldReturnStateSignInErrorWhenUserHasFailedToSignIn() = runTest {
        val userService = object: UserService {
            override suspend fun signInUser(request: SignInRequestModel): SignInResponse {
                return SignInResponse(login = null)
            }
        }
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()

        //When
        AppStream.send(SignInRequest(email, password))

        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(UserState.SignInError(""), currentState)
        job.cancel()
    }


    @Test
    fun itShouldReturnStateSignInSuccessWhenUserHasSuccessfullySignedUp() = runTest {
        val userService = object: UserService {
            override suspend fun signUpUser(request: SignUpRequestModel): SignUpResponseModel {
                return SignUpResponseModel(registerUser = SignUpResponseModel.RegisterUser(sampleAuthToken, dateOfBirth, email, firstName, gender, 1, imageUrl, lastName))
            }
        }
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()

        //When
        AppStream.send(SignUpRequest(firstName, lastName, email, password,gender, dateOfBirth, imageUrl))

        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(UserState.SignInSuccess(email, sampleAuthToken), currentState)
        job.cancel()
    }

    @Test
    fun itShouldReturnStateProfileFetchSuccessWhenUserHasSuccessfullyFetchedProfile() = runTest {
        val swikoUser: GetUserResponseModel.SwikoUser? = GetUserResponseModel.SwikoUser(sampleAuthToken, dateOfBirth, email, firstName, gender, 1, imageUrl, lastName)
        val userResponse: GetUserResponseModel = GetUserResponseModel(swikoUser = listOf(swikoUser))

        tokenCache.saveToken(sampleAuthToken)

        val userService = object: UserService {
            override suspend fun getUserProfile(authToken: String): GetUserResponseModel {
                return userResponse
            }
        }
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()

        //When
        AppStream.send(ProfileFetchRequest)

        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(UserState.ProfileFetchSuccess(userResponse), currentState)
        job.cancel()
    }

    @Test
    fun itShouldReturnStateProfileEditSuccessWhenUserHasSuccessfullyEditedProfile() = runTest {
        val returning = EditProfileResponseModel.UpdateSwikoUser.User(dateOfBirth, email, firstName, gender, imageUrl, lastName, sampleAuthToken)
        val updatedResponse = EditProfileResponseModel.UpdateSwikoUser(returning = listOf(returning))
        val userResponse = EditProfileResponseModel(updateSwikoUser = updatedResponse)

        tokenCache.saveToken(sampleAuthToken)

        val userService = object: UserService {
            override suspend fun editUserProfile(request: EditProfileRequestModel): EditProfileResponseModel {
                return userResponse
            }
        }
        val localisationProvider = object : LocalisationProvider {}

        val actor = TheActor.run {
            userActor(localisationProvider, userService, tokenCache).toActor(UserState.NotSignedIn)
        }

        val job = actor.start(this)
        advanceUntilIdle()

        //When
        AppStream.send(EditProfileRequest(firstName, lastName, email, gender, dateOfBirth, imageUrl))

        //Then
        val currentState = getCurrentStateOfActor()
        Assert.assertEquals(UserState.ProfileEditSuccess("Profile Updated Successfully!!!"), currentState)
        job.cancel()
    }

    private suspend fun getCurrentStateOfActor(): UserState {
        val stateResponse = CompletableDeferred<UserState>()
        AppStream.send(GetUserState(stateResponse))
        val currentState = stateResponse.await()
        return currentState
    }

}