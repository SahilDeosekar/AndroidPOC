package net.srijan.swiko

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.actor.route.Navigate
import net.srijan.swiko.ui.actor.route.RouteState

/**
 * Created by Furkan on 18.04.2020
 */

fun NavController.popBackStackAllInstances(destination: Int, inclusive: Boolean): Boolean {
    var popped: Boolean
    while (true) {
        popped = popBackStack(destination, inclusive)
        if (!popped) {
            break
        }
    }
    return popped
}

fun Fragment.navigate(fragment: Fragment, path: String) {
    lifecycleScope.launch {
        AppStream.send(Navigate(fragment, path))
    }
}