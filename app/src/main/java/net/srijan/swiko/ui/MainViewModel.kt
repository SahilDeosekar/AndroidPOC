package net.srijan.swiko.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.actor.route.GetRouteState
import net.srijan.swiko.ui.actor.route.RouteState

sealed class NavigationUIMessages {
    data class SelectTabAndNavigate( val tabIndex: Int, val nestedPath: String ) : NavigationUIMessages()
    object SkipNavigation : NavigationUIMessages()
}
class MainViewModel : ViewModel() {

    private val _uiMessagesFlow: MutableSharedFlow<NavigationUIMessages> =
        MutableSharedFlow(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uiMessages = _uiMessagesFlow.asSharedFlow()


    suspend fun getNavigationState() {

        when (val state = fetchNavigationState()) {
            is RouteState.NavigateToTab -> _uiMessagesFlow.emit(NavigationUIMessages.SelectTabAndNavigate(state.tabIndex,state.nestedPath))
            RouteState.NotNavigated -> _uiMessagesFlow.emit(NavigationUIMessages.SkipNavigation)
            else -> {
                _uiMessagesFlow.emit(NavigationUIMessages.SkipNavigation)
            }
        }

    }

    private suspend fun fetchNavigationState(): RouteState {
        val defState = CompletableDeferred<RouteState>()
        AppStream.send((GetRouteState(defState)))
        return defState.await()
    }
}
