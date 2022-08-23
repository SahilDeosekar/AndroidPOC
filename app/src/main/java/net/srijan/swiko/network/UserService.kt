package net.srijan.swiko.network

import net.srijan.swiko.model.request.profile.EditProfileRequestModel
import net.srijan.swiko.model.request.auth.SignInRequestModel
import net.srijan.swiko.model.request.auth.SignUpRequestModel
import net.srijan.swiko.model.response.profile.EditProfileResponseModel
import net.srijan.swiko.model.response.profile.GetUserResponseModel
import net.srijan.swiko.model.response.auth.SignInResponse
import net.srijan.swiko.model.response.auth.SignUpResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("signin")
    suspend fun signInUser(@Body request: SignInRequestModel): SignInResponse {
        TODO("Not implemented")
    }

    @POST("signup")
    suspend fun signUpUser(@Body request: SignUpRequestModel): SignUpResponseModel {
        TODO("Not implemented")
    }

    @GET("getUser")
    suspend fun getUserProfile(@Query(value = "authToken") authToken: String): GetUserResponseModel {
        TODO("Not implemented")
    }

    @POST("editProfile")
    suspend fun editUserProfile(@Body request: EditProfileRequestModel): EditProfileResponseModel {
        TODO("Not implemented")
    }
}