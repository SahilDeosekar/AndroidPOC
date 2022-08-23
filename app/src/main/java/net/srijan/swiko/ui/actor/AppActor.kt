package net.srijan.swiko.ui.forceupdate

import kotlinx.coroutines.CompletableDeferred
import net.srijan.swiko.network.AppVersionService
import net.srijan.swiko.stream.Message
import net.srijan.swiko.stream.State
import net.srijan.swiko.ui.actor.ActorFunction
import net.srijan.swiko.ui.actor.TheActorScope

object AppVersionRequest : Message
data class GetAppVersionState(val state: CompletableDeferred<AppState> = CompletableDeferred()) : Message
object PrintCurrentState : Message

sealed class AppState : State {
    data class AppVersion(val appVersion: String) : AppState()
    object AppVersionError : AppState()
    object AppVersionNotFetched: AppState()
}

fun appActor(appVersionService: AppVersionService): ActorFunction<AppState> {

    suspend fun getAppVersion(): AppState {
        return try {

            val appVersionResponse = appVersionService.getAppVersion()
            val appVersionAndroid = appVersionResponse.swikoAppSettings?.firstOrNull()?.appVersionAndroid

            if (appVersionAndroid.isNullOrEmpty()) {
                AppState.AppVersionError
            } else {
                AppState.AppVersion(appVersionAndroid)
            }
        } catch (e: Exception) {
            AppState.AppVersionError
        }
    }

    tailrec suspend fun TheActorScope.appfunction(state: AppState) {
        when (val message = channel.receive()) {
            is AppVersionRequest -> {
                val user = getAppVersion()
                appfunction(user)
            }

            is GetAppVersionState -> {
                message.state.complete(state)
                appfunction(state)
            }
            is PrintCurrentState -> {
                println("AppActor: State - "+state)
                appfunction(state)
            }
            else -> appfunction(state)

        }
    }
    return TheActorScope::appfunction
}