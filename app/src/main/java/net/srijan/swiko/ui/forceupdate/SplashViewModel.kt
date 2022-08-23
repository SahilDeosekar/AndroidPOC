package net.srijan.swiko.ui.forceupdate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.stream.AppStream.send

class SplashViewModel : ViewModel() {

    private val _uiMessagesFlow: MutableSharedFlow<AppVersionUiMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()

    suspend fun getAppVersion() {
        send(PrintCurrentState)
        send(AppVersionRequest)
        getAppVersionState()
        send(PrintCurrentState)
    }

    private suspend fun getAppVersionState() {

        when (val state = fetchAppVersionState()) {
            is AppState.AppVersion -> _uiMessagesFlow.emit(AppVersionUiMessages.AppVersion(state.appVersion))
            AppState.AppVersionError -> _uiMessagesFlow.emit(AppVersionUiMessages.AppVersionError)
            else -> {
                _uiMessagesFlow.emit(AppVersionUiMessages.AppVersionError)
            }
        }

    }

    private suspend fun fetchAppVersionState(): AppState {
        val defState = CompletableDeferred<AppState>()
        send(GetAppVersionState(state = defState))
        return defState.await()
    }

}