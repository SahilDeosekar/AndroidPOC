package net.srijan.swiko.network

import net.srijan.swiko.model.response.forceupdate.AppVersionResponse
import retrofit2.http.GET

interface AppVersionService {
    @GET("getAppVersion")
    suspend fun getAppVersion(): AppVersionResponse
}