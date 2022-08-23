package net.srijan.swiko.model.response.forceupdate


import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("swiko_app_settings")
    val swikoAppSettings: List<SwikoAppSettings?>?
) {
    data class SwikoAppSettings(
        @SerializedName("app_version_android")
        val appVersionAndroid: String?
    )
}