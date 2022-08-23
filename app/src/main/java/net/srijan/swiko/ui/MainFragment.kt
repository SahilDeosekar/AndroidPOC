package net.srijan.swiko.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import net.srijan.swiko.R
import net.srijan.swiko.databinding.FragmentMainBinding
import net.srijan.swiko.databinding.FragmentSplashBinding
import net.srijan.swiko.ui.actor.ScopeReference
import net.srijan.swiko.ui.actor.TheActor
import net.srijan.swiko.ui.actor.TheActor.Companion.toActor
import net.srijan.swiko.ui.actor.route.RouteState
import net.srijan.swiko.ui.actor.route.routeActor


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            mainViewModel.uiMessages.collect {
                when(it) {
                    is NavigationUIMessages.SelectTabAndNavigate -> {
                        binding.bottomNavigation.selectedItemId =
                            binding.bottomNavigation.menu.getItem(it.tabIndex).itemId
                        binding.fragmentContainer.findNavController().navigate("myapp://${it.nestedPath}".toUri())
//                        findNavController().navigate("myapp://${it.nestedPath}")
                    }
                    NavigationUIMessages.SkipNavigation -> {}
                }
            }
        }
    }

    private fun init() {

        val bottomNavView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController =  navHostFragment.navController

        val mainNavGraph = navController.navInflater.inflate(R.navigation.app_navigation)
        val profileFragmentNode = mainNavGraph.nodes.get(R.id.profileFragment)
        profileFragmentNode?.addDeepLink("myapp://home/profile")
        val resetPasswordNode = mainNavGraph.nodes.get(R.id.resetPasswordFragment)
        resetPasswordNode?.addDeepLink("myapp://notification/resetPassword")

        val dashboard = navController.graph.findNode(R.id.home_navigation)
        mainNavGraph.remove(profileFragmentNode!!)
        (dashboard as NavGraph).addDestination(profileFragmentNode)

        val notifications = navController.graph.findNode(R.id.notification_navigation)
        mainNavGraph.remove(resetPasswordNode!!)
        (notifications as NavGraph).addDestination(resetPasswordNode)




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_navigation, R.id.search_navigation,R.id.games_navigation, R.id.notification_navigation
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

        with(ScopeReference(CoroutineScope(Dispatchers.Main + Job()))) {
            TheActor.run {
                routeActor().toActor(RouteState.NotNavigated)
            }.start()
        }

    }

}