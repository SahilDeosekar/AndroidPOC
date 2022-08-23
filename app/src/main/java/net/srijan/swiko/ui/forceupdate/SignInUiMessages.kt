package net.srijan.swiko.ui.forceupdate

sealed class AppVersionUiMessages {
    data class AppVersion(val appVersion:String): AppVersionUiMessages()
    object AppVersionError: AppVersionUiMessages()
}
