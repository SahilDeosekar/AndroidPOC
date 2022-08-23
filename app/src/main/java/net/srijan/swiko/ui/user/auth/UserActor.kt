package net.srijan.swiko.ui.user.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import net.srijan.swiko.AppConfig
import net.srijan.swiko.model.request.profile.EditProfileRequestModel
import net.srijan.swiko.model.request.auth.SignInRequestModel
import net.srijan.swiko.model.request.auth.SignUpRequestModel
import net.srijan.swiko.model.response.profile.GetUserResponseModel
import net.srijan.swiko.network.UserService
import net.srijan.swiko.stream.Message
import net.srijan.swiko.stream.State
import net.srijan.swiko.ui.actor.ActorFunction
import net.srijan.swiko.ui.actor.TheActorScope
import net.srijan.swiko.ui.user.LocalisationProvider
import kotlin.Exception


data class SignInRequest(val username: String, val password: String) : Message
data class GetUserState(val state: CompletableDeferred<UserState> = CompletableDeferred()) : Message

data class SignUpRequest(
    val firstName: String, val lastName: String, val email: String,
    val password: String, val gender: String, val dob: String,
    val imageUrl: String
) : Message

object ProfileFetchRequest : Message
data class EditProfileRequest(
    val firstName: String, val lastName: String, val email: String,
    val gender: String, val dob: String,
    val imageUrl: String) : Message

sealed class UserState : State {
    data class SignInSuccess(val username: String, val userToken: String?) : UserState()
    data class SignInError(val error: String) : UserState()
    object NotSignedIn : UserState()
    data class ProfileFetchSuccess(val user: GetUserResponseModel?) : UserState()
    data class ProfileFetchFailure(val error: String) : UserState()
    data class ProfileEditSuccess(val message: String): UserState()
}

suspend fun <T> T?.also(block:suspend (T) -> Unit){
    if (this !== null){
        block(this)
    }
}

fun userActor(provider: LocalisationProvider, userService: UserService, tokenCache: TokenCache): ActorFunction<UserState> {


    suspend fun performSignIn(username: String, password: String): UserState {
        return try {
            val userSignInRequest = SignInRequestModel(email = username, password = password)
            val signInResponse = userService.signInUser(userSignInRequest)
            if (signInResponse.login?.email.isNullOrEmpty()) {
                UserState.SignInError(provider.getSignInError())
            } else {

                signInResponse.login?.authToken?.also(tokenCache::saveToken)
                UserState.SignInSuccess(
                    signInResponse.login?.email ?: username,
                    signInResponse.login?.authToken
                )
            }
        } catch (e: Exception) {
            UserState.SignInError(provider.getSignInError())
        }
    }

    suspend fun performSignUp(
        firstName: String, lastName: String, email: String,
        password: String, gender: String, dob: String,
        imageUrl: String
    ): UserState {
        return try {
            val signUpRequest = SignUpRequestModel(
                firstName = firstName, lastName = lastName, email = email,
                password = password, gender = gender, dob = dob, imageUrl = imageUrl
            )
            val signUpResponse = userService.signUpUser(signUpRequest)
            if (signUpResponse.registerUser == null) {
                UserState.SignInError(provider.getSignUpError())
            } else {
                signUpResponse.registerUser.authToken?.also(tokenCache::saveToken)
                UserState.SignInSuccess(
                    signUpResponse.registerUser.email ?: email,
                    signUpResponse.registerUser.authToken
                )
            }
        } catch (e: Exception) {
            UserState.SignInError(provider.getSignUpError())
        }
    }

    suspend fun fetchUserProfile(): UserState {
        return try {
            val token = tokenCache.readToken()
            if (token != null) {
                val profileResponse = userService.getUserProfile(token)
                if (profileResponse.swikoUser.isNullOrEmpty()) {
                    UserState.ProfileFetchFailure(provider.getProfileFetchError())
                } else {
                    UserState.ProfileFetchSuccess(profileResponse)
                }
            } else {
                UserState.ProfileFetchFailure(provider.getProfileFetchError())
            }
        } catch (e: Exception) {
            UserState.ProfileFetchFailure(provider.getProfileFetchError())
        }
    }

    suspend fun editUserProfile(
        firstName: String, lastName: String, email: String,
        gender: String, dob: String, imageUrl: String
    ): UserState {
        return try {
            val token = tokenCache.readToken()
            if (token != null) {
                val editProfileRequest = EditProfileRequestModel(firstName = firstName, authToken = token, lastName = lastName, email = email, gender = gender, dob = dob, imageUrl = imageUrl)
                val editProfileResponse = userService.editUserProfile(editProfileRequest)
                if (editProfileResponse.updateSwikoUser?.returning.isNullOrEmpty()) {
                    UserState.ProfileFetchFailure(provider.getEditProfileError())
                } else {
                    UserState.ProfileEditSuccess("Profile Updated Successfully!!!")
                }
            } else {
                UserState.ProfileFetchFailure(provider.getEditProfileError())
            }
        } catch (e: Exception) {
            UserState.ProfileFetchFailure(provider.getEditProfileError())
        }
    }

    tailrec suspend fun TheActorScope.function(state: UserState) {
        val message = channel.receive()
        when (message) {
            is SignInRequest -> {
                val user = performSignIn(message.username, message.password)
                function(user)
            }

            is GetUserState -> {
                message.state.complete(state)
                function(state)
            }

            is SignUpRequest -> {
                val user = performSignUp(
                    message.firstName, message.lastName, message.email,
                    message.password, message.gender, message.dob, message.imageUrl
                )
                function(user)
            }

            is ProfileFetchRequest -> {
                val profile = fetchUserProfile()
                function(profile)
            }

            is EditProfileRequest -> {
                val profile = editUserProfile(message.firstName, message.lastName, message.email, message.gender, message.dob, message.imageUrl)
                function(profile)
            }

            else -> function(state)
        }
    }
    return TheActorScope::function
}

interface TokenCache{
    suspend fun readToken():String?
    suspend fun saveToken(token:String):Unit
}



suspend fun saveAuthToken(appConfig: AppConfig, token: String?) {
    token?.let {
        appConfig.setUserToken(it)
    }
}