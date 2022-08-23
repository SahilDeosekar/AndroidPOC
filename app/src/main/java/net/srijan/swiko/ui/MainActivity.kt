package net.srijan.swiko.ui

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavGraph
import androidx.navigation.findNavController

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.srijan.swiko.*
import net.srijan.swiko.databinding.ActivityMainBinding
import net.srijan.swiko.ui.actor.ScopeReference
import net.srijan.swiko.ui.actor.TheActor
import net.srijan.swiko.ui.actor.route.RouteState
import net.srijan.swiko.ui.actor.route.routeActor


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var navController: NavController

    lateinit var appConfig: AppConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfig = AppConfig(this.dataStore)
        setTheme(R.style.Theme_Swiko)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        handleIncomingIntent(intent)
//    }
//
//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount == 1) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//
//    private fun handleIncomingIntent(intentData: Intent?) {
//        lifecycleScope.launch {
//            if (!appConfig.userToken.firstOrNull().isNullOrEmpty()) {
//                if (intentData?.data.toString().contains("www.example.com/search")) {
//                    replaceFragment(SearchFragment())
//                } else if (intentData?.data.toString().contains("www.example.com/games")) {
//                    replaceFragment(GamesFragment())
//                }
//            } else {
//                replaceFragment(SplashFragment(),true)
//            }
//        }
//    }


}