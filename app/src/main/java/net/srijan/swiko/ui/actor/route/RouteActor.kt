package net.srijan.swiko.ui.actor.route

import android.util.Log
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CompletableDeferred
import net.srijan.swiko.R
import net.srijan.swiko.stream.Message
import net.srijan.swiko.stream.State
import net.srijan.swiko.ui.MainFragment
import net.srijan.swiko.ui.actor.ActorFunction

import net.srijan.swiko.ui.actor.TheActorScope
import net.srijan.swiko.ui.actor.route.RouteState.*

data class GetRouteState(
    val state: CompletableDeferred<RouteState> = CompletableDeferred()
) : Message

data class Navigate(val fragment: Fragment, val path: String) : Message

sealed class RouteState : State {
    object NotNavigated : RouteState()
    data class NavigateToTab(val tabIndex: Int, val nestedPath: String): RouteState()
    object NavigatedSuccessfully: RouteState()

}




fun routeActor(): ActorFunction<RouteState> {
    var screens: MutableList<String>
    fun performNavigation(fragment: Fragment, path: String): RouteState {
        Log.d("DIGITAL_SERVICE", fragment.toString())

         screens = path.split("/") as MutableList<String>
        if (screens[0] == "bottomTab") {
            when (screens[1]) {
                "home" -> {
                    if (screens[2] == "profile") {
//                        val navOptions =
//                            NavOptions.Builder()
//                                .setPopUpTo(
//                                    R.id.nav_graph_main,
//                                    false
//                                )
//                                .setEnterAnim(R.anim.slide_in_right)
//                                .setExitAnim(R.anim.slide_out_left)
//                                .setPopEnterAnim(R.anim.slide_in_left)
//                                .setPopExitAnim(R.anim.slide_out_right)
//                                .build()

//                        fragment.findNavController().navigate(R.id.home_navigation)
                        return NavigateToTab(0,"${screens[1]}/profile")
                    }
                }

                "notification" -> {
                    if (screens[2] == "resetPassword") {
//                        val navOptions =
//                            NavOptions.Builder()
//                                .setPopUpTo(
//                                    R.id.nav_graph_main,
//                                    false
//                                )
//                                .setEnterAnim(R.anim.slide_in_right)
//                                .setExitAnim(R.anim.slide_out_left)
//                                .setPopEnterAnim(R.anim.slide_in_left)
//                                .setPopExitAnim(R.anim.slide_out_right)
//                                .build()

//                        fragment.findNavController().navigate(R.id.home_navigation)
//                        fragment.findNavController()
//                            .navigate("myapp://${screens[1]}/${screens[2]}".toUri())
                        return NavigateToTab(3,"${screens[1]}/${screens[2]}")
                    }
                }
                else -> { return NotNavigated }
            }
        }
        return NotNavigated
    }

    tailrec suspend fun TheActorScope.function(state: RouteState) {
        when (val message = channel.receive()) {
            is Navigate -> {
                val navState = performNavigation(message.fragment, message.path)
                function(navState)
            }

            is GetRouteState -> {
                message.state.complete(state)
                function(state)
            }

            else -> function(state)
        }
    }
    return TheActorScope::function

}